import asyncio
import websockets

def runWebsocket():

    limitVar = 500

    async def handler(websocket, path):
        print("connection")
        data = await websocket.recv()

        print("Data Recieved:", data)

        reply = str([420,69,1337])
        print("replying with:", reply)

        await websocket.send(reply)

    start_server = websockets.serve(handler, "", 8001)
    asyncio.get_event_loop().run_until_complete(start_server)
    asyncio.get_event_loop().run_forever()

runWebsocket()