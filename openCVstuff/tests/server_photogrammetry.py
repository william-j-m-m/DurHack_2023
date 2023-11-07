import threading
import numpy as np
from skimage.draw import disk
import cv2
import time
import asyncio
import websockets
import copy

camID = 2

pSrc = [(  559,162),( 1447 , 146),(1817 , 931),(  170 , 956)] ###SET THIS TO CORNERS OF GROUND TO WATCH
pDst = [(  0,0),( 1000, 0 ),( 1000 , 1000),(  0 , 1000)]



dst = np.zeros((1000,1000),dtype=np.uint8)



objectPermananceDistanceThreshold = 250
keepAliveAfterLossLimit = 5

def generateCircle(r):
    mask = np.zeros(((r*2)+1, (r*2)+1), dtype=np.uint8)
    rr, cc = disk((r, r), r)
    mask[rr, cc] = 1
    return mask

cv2.startWindowThread()
cap = cv2.VideoCapture(camID)

ret, prev = cap.read()

cv2.imshow("Preview", prev)

lowerPurple = np.array([100, 100, 10])
upperPurple = np.array([170, 200, 255])

opening_kernel = np.ones((10,10),np.uint8)

dilation_kernel = generateCircle(50)

params = cv2.SimpleBlobDetector_Params()

params.minDistBetweenBlobs = 50.0
params.filterByInertia = False
params.filterByConvexity = False
params.filterByColor = False
params.filterByCircularity = False
params.filterByArea = True
params.minArea = 20.0
params.maxArea = 1000000000

# Create a detector with the parameters
detector = cv2.SimpleBlobDetector_create(params)

font = cv2.FONT_HERSHEY_SIMPLEX


def getDistanceBetweeenPoints(p1, p2):
    x1,y1 = p1
    x2,y2 = p2
    xdiff = x1-x2
    ydiff = y1-y2
    return int(np.sqrt((xdiff**2) + (ydiff**2)))




def doImageRecognition():


    global keypoints
    global frame
    global readyPoints
    global readyPointsLi

    rawXYPoints = []
    idcounter = 1

    while(True):
        ret, frameunKeyed = cap.read()

        imD = frameunKeyed.copy()
        dstD = dst.copy()

        if len(pSrc)==4 and len(pDst)==4:
            H = cv2.findHomography(np.array(pSrc,dtype=np.float32),np.array(pDst,dtype=np.float32),cv2.LMEDS)
            frame=cv2.warpPerspective(imD,H[0],(dstD.shape[1],dstD.shape[0]))


        hsvframe = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV) 
        mask = cv2.inRange(hsvframe, lowerPurple, upperPurple)
        opening = cv2.morphologyEx(mask, cv2.MORPH_OPEN, opening_kernel)
        dilation  = cv2.dilate(opening, dilation_kernel, iterations=1)













        # Detect blobs
        keypoints = detector.detect(dilation)
        # Draw detected blobs as green circles
        # img_with_keypoints = cv2.drawKeypoints(frame, keypoints, np.array([]), (0, 255, 0), cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)
        # Show the image with detected blobs
        # cv2.imshow("Blobs", img_with_keypoints)
        # cv2.imshow('4 - dilation',dilation)
        # cv2.imshow('3 - opening',opening)
        # cv2.imshow('2 - mask',mask)
        # cv2.imshow('1 - frame',frame)
        # if cv2.waitKey(1) & 0xFF == ord('q'):
        #     break



        ###  STRUCTURE IS ID, X, Y, ITERATIONSAGO
        previousPointsList = rawXYPoints

        for i in range(len(previousPointsList)):
            if i<len(previousPointsList) and previousPointsList[i][3] > keepAliveAfterLossLimit:
                previousPointsList.pop(i)
                i-=1


        rawXYPoints = []

        for p in keypoints:
            x,y = p.pt
            x = int(x)
            y = int(y)
            rawXYPoints.append([None,x,y,0])

        for p in rawXYPoints:
            distances = []
            if len(previousPointsList) == 0:
                p[0] = idcounter
                idcounter += 1
            else:
                for q in previousPointsList:
                    distances.append(getDistanceBetweeenPoints((p[1],p[2]), (q[1],q[2])))
                minDist = 1000000
                for i in range(len(distances)):
                    if distances[i] < minDist:
                        minDist = distances[i]
                        minDistIndex = i
                if minDist > objectPermananceDistanceThreshold:
                    p[0] = idcounter
                    idcounter += 1
                else:
                    p[0] = previousPointsList.pop(minDistIndex)[0]
        
        if len(previousPointsList) != 0:
            for p in previousPointsList:
                p[3] += 1
                rawXYPoints.append(p)

            if p[0] == None:
                print("Something doesnt have an ID!!!") 

        readyPointsPrep = ""
        for p in rawXYPoints:
            id = str(p[0])
            x = str(p[1])
            y = str(p[2])
            readyPointsPrep += id + "," + x + "," + y + ";"
        readyPoints = readyPointsPrep
        readyPointsLi = copy.copy(rawXYPoints)




def printKeyPointsLoop():
    global keypoints
    while(True):
        for kp in keypoints:
            x,y = kp.pt
            x = int(x)
            y = int(y)
            print("x="+str(x)+"    y="+str(y))
            


imageThread = threading.Thread(target=doImageRecognition, args=())
printThread = threading.Thread(target=printKeyPointsLoop, args=())


imageThread.start()

time.sleep(0.5)



async def handler(websocket, path):
    data = await websocket.recv()
    print("Recieved [", data, "] - Now streaming person coords.")
    while (True):
        await websocket.send(readyPoints)
        time.sleep(0.1)

start_server = websockets.serve(handler, "", 8001)
asyncio.get_event_loop().run_until_complete(start_server)

websocketThread = threading.Thread(target=asyncio.get_event_loop().run_forever, args=())
websocketThread.start()


while(True):
    time.sleep(0.1)
    mkupImage = copy.copy(frame)
    for p in readyPointsLi:
        mkupImage = cv2.circle(mkupImage, (p[1],p[2]), 15, (0,255,0), -1)
        label = "ID=" + str(p[0])
        mkupImage = cv2.putText(mkupImage, label, (p[1]-40,p[2]+45), font, 1, (0,255,0), 2, cv2.LINE_8)



    cv2.imshow("Durcraft Virtual Attendees", mkupImage)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
quit()