# LionUtils
This Plugin provides improvements for LionAPI and ensures the feeling of a custom server by adding various design elements<br>

# Features
<br>
<details>
<summary>reset</summary>
/reset
Allows you to reset different parts of the server. <br>
Be careful as this can delete Survival worlds forever.<br>
Info: To work properly, the World reset needs a startup script to be set (spigot.yml, settings.restart-script). Otherwise, you have to manually start the server again each time you use the command.

</details>
<details>
<summary>status</summary>
Allows you to edit the Status in the Tab List. <br>
It is highly recommended to use the UI for Status management as it provides way more features than the command. (accessible through lionAPI Main Menu or /status) 
You can set your own status, select a Global Status or use a dynamiv Status like your Teamname

</details>
<details>
<summary>Advanced Player Management</summary>
Allows operators to manage different things a Player can or cannot do, like fly (also in survival), take damage, use the chat and more.<br>
These settings are accessible through the lionAPI Main Menu.
The UI provides an option to set global settings and create custom settings for single players.

</details>
<details>
<summary>Inventory Viewer</summary>
/inv <player><br>
Allows Operators to open Inventorys of every Player on the Server, as well as adding and removing Items.<br>
Includes support for Offhand, Armor slots and the Ender Chest.<br>
Be Careful with mods that sort inventories, as this could cause Item Duplication

</details>
<details>
<summary>Flyspeed</summary>
/flyspeed <br>
Allows users that can fly to set their fly speed. <br>
Range between -10 and 10.<br>
If you set it to a negative value, this will inverty your direction (Idk what this could be useful for but it was fun while testing xd)

</details>
<details>
<summary>MOTD</summary>
Allows you to set custom MOTDs for when the server is empty and when the server has players on it, including the option to add a random player's name to the text. 

</details>
