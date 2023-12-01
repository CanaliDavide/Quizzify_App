const net = require('net');

let lobby = [];

const server = net.createServer();

server.on('connection', (clientSocket) => {
  console.log(`Connection accepted: ${clientSocket.remoteAddress}`);

  if(lobby.length == 1){
    if (lobby[0].destroyed){
      lobby = []
    }
  }

  lobby.push(clientSocket);

  handleClient(clientSocket, lobby);
  
  if (lobby.length === 2) {
    handleLobby(lobby);
    lobby = [];
  }
});

function handleClient(client, lobby){
  client.on('end', () => {
    console.log(`Connessione chiusa da ${client.remoteAddress}`);
  });
}

function handleLobby(clients) {
  console.log('New Lobby');
  clients[0].write('Generate\n');
  clients[1].write('Loading\n');
  console.log(`Init messages sended`);
  var alreadyClosed = false;

  clients.forEach((client) => {    
  let receivedData = '';
  client.on('data', (data) => {
    receivedData += data.toString();

    if (receivedData.includes('E-O-M')) {

      var message = receivedData.toString().trim();
      receivedData = '';

      var index = message.indexOf("---")
      var prefix
      if (index >= 0) {
          prefix = message.substring(0, index)
          message = message.substring(index + 3).slice(0,-5) // Skip the "---"
      }else{
          prefix = message.slice(0,-5)
      }
      console.log(`Message received from ${client.remoteAddress}: ${prefix}`);

      switch(prefix){
        case "Questions":
          var response = "Create---"+message
          clients.forEach((otherClient) => {
            
            if(otherClient !== client) 
            {otherClient.write(response+"\n")}
            
            //otherClient.write(response+"\n")
          })
          console.log(`Message Create Send`);
          break;
        case "Ready":
          clients.forEach((otherClient) => {
            otherClient.write("Start"+"\n")
          })
          console.log(`Message Start Send`);
          break;
        case "Update":
          var response = "Update---"+message
          clients.forEach((otherClient) => {
            
            if(otherClient !== client) 
            {otherClient.write(response+'\n')}
            /*
            otherClient.write(response+'\n')
            */
            console.log(`Message Update Send to ${client.remoteAddress}`);
          })
          break;
        case "End":
          var response = "End---"
          alreadyClosed = true;
          clients.forEach((otherClient) => {
            
            if(otherClient !== client) {
              if(message === "true"){
                otherClient.write(response+"false"+'\n')
              }else{
                otherClient.write(response+"true"+'\n')
              }
            }else{
              if(message === "true"){
                otherClient.write(response+"true"+'\n')
              }else{
                otherClient.write(response+"false"+'\n')
              }
            }
            console.log(`Message End Send to ${client.remoteAddress}`);
          })
          break;
      }
    }
    });

    client.on('end', () => {
      console.log(`Connessione chiusa da ${client.remoteAddress}`);
        if (!alreadyClosed){
          var response = "End---"
          clients.forEach((otherClient) => {
              if(otherClient !== client) {
                  otherClient.write(response+"true"+'\n')
              }
          })
        }
    });
  });
}

server.listen(30000, () => {
  console.log('Server listening on port 30000!');
});
