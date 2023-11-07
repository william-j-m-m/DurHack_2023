# DurHack_2023 - Durcraft Virtual Attendees
Our project for DurHack 2023 (Won 2nd Place Overall)

See the project summary on [DevPost](https://devpost.com/software/durcraft-virtual-attendees).

**Warning - the code you're about to see is similar in look to a bowl of spaghetti, but such is the way with 24 hours Hackathon projects. No code has been refactored since the event! Please enjoy.**

<p float="left">
  <img src="https://d112y698adiu2z.cloudfront.net/photos/production/software_photos/002/655/180/datas/gallery.jpg" width="49%" />
  <img src="https://d112y698adiu2z.cloudfront.net/photos/production/software_photos/002/655/179/datas/gallery.jpg" width="49%" /> 
</p>
Left - The Tech setup. One Laptop is the Vision Server, the other is the Game Server and is also running the game client.<br><br>



<p style="text-align: center">
<b>Click the image below to watch a demo video</b><br><br>
<a href="https://youtu.be/5xrsdYBfODg?si=L2VoODrBZpT0qs8w"><img alt="Demo Video Link" src="https://img.youtube.com/vi/5xrsdYBfODg/0.jpg"></a>
</p>

*The following is our summary from [DevPost](https://devpost.com/software/durcraft-virtual-attendees)*

## Inspiration
We were inspired by how dull online events can sometimes be, especially when run over Microsoft teams or similar platforms. We wanted to find a way to give those who cannot attend an event physically the same experience online! This proof-of-concept shows how a crowd of physical people can be brought into the virtual space.

## What it does
Our software takes a real time video feed from a camera, and represents physical Durhack attendees in a virtual re-creation of the space! As people walk around in the lobby (wearing their purple durhack 2023 T-shirts), they appear as players in a Minecraft server, which any unmodified Minecraft client can connect to!

## How we built it
We used a mirrorless camera and HDMI capture card to get a real-time video feed into python/opencv. This applies a variety of image processing filters to get a “top-down view “ video and pick out areas of purple, before identifying them and sending them to another computer via websockets.

This computer, running the Minecraft server and custom plugin written in java, creates, moves and deletes NPC entities according to the data received.

## Challenges we ran into
We ran into challenges almost every step of the way - from getting video into the vision computer to running Minecraft servers on Eduroam.

Significant issues included;
- The maths involved in translating the coordinates on the camera plane onto the floor (which was later abandoned in favour of a computationally faster photogrammetric approach). There’s a lot of unhelpful information about this online!
- Keeping websockets between the Vision and Game server open
- Detecting people reliably with built-in opencv methods (leading us to create our own t-shirt based algorithm)

## Accomplishments that we're proud of
We’re proud of the whole thing! The final payoff of seeing the project work, and the awe on the faces of passers by made it worth the (literal) all nighter coding.

We’re also particularly proud of our hackathon ‘side-project’ - [duck.technology](https://duck.technology)! (domain courtesy of godaddy registrar)

## What we learned
An awful lot about openCV and image recognition, trigonometry, vectors, polar coordinates, and how much Redbull is too much Redbull.

## What's next for Durcraft Virtual Attendees?
We worked on getting the rotation of the in-game characters to follow real-world movement, however any success was hindered by poor performance and minimal time to optimise. Maybe with some more thought we could implement this feature properly.

Next year, we have our eye on some hardware projects. Did you know you can buy a de-commissioned traffic light on eBay for £80….?

Demonstration of project can be found [here](https://youtu.be/5xrsdYBfODg?si=L2VoODrBZpT0qs8w)