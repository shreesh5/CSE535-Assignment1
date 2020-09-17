# CSE535-Assignment1

To run the code:  

1. Open the project in Android Studio  
2. Connect a mobile phone to the computer and build the app on the phone  

In the app:  

1. Select the gesture  
2. On the second screen, press "PLAY" to view the video.  
3. Press "PRACTICE" to go to the next screen in order to record the video.  
4. On the third screen, select the name of the last name of the student in the first dropdown menu.  
5. On the third screen, in the first text field, enter the practice number.  
6. On the third screen, in the second text field, enter 1 for "Accept" and 0 for "Reject".  
7. After both these fields are filled, press "RECORD" to be taken to the camera interface.  
8. After recording the video, click on "UPLOAD" to upload the file to the server.  

Note:  
--- The ASU ID is matched to the last name of the student.  
--- Camera and Storage permissions will need to be granted to the app.  
--- If 1 is entered as the value for "Accept" then the file will be saved to the accept folder. Otherwise it will be saved to the reject folder.  


About Our Server Model:  

We built our own local server on a laptop by using XAMPP. XAMPP was installed on Pop_OS 19.04. The XAMPP server was set up to listen to port 8080.
Since it is a local server, the URL for uploading to the server is constantly changing depending on the IP address of the laptop. Hence the current URL which is present in the Android Studio Code will not work.
