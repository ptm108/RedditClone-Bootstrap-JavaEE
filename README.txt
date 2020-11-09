Name: Phang Tze Ming
Matric No.: A0184247Y

Enhancements from Assignment 1:
- Used Bootstrap UI library and FontAwesome Icons
- Redesigned login and register pages, closer to Reddit's design
- Custom css colours to match Reddit's dark mode


Feature Enhancement/Additional Features:

Users: 
- Passwords are hashed with salt using PBKDF2 algorithm 
- User can enter a description about themselves on their profile
- Users can change their display names (no effect on username used for login)
- Users can change password

Community:
- Users can navigate to localhost:8080/RedditClone-war/r/<community-name> to navigate to a community page as per Reddit's design. If community does not exit, user is prompted to create a new one. 
- User who creates the community becomes a moderator by default. Moderators can edit communities. 

Posts:
- Users are able to undo their voting for posts by clicking on their original voted button, as per reddit's design. 
- Posts show <time> ago instead of time stamp
- Links to appropriate user profiles/communities are added over the posts

Comments:
- Comments posted shows <time> ago instead of time stamp
