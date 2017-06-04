<img align="right" src="https://gnarbot.xyz/assets/img/logo.jpg" height="250" width="250">

# Gnar-bot [![Dependency Status](https://www.versioneye.com/user/projects/58f140fc9f10f8003f8856c5/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/58f140fc9f10f8003f8856c5) [![License](https://img.shields.io/github/license/mashape/apistatus.svg?style=flat-square)](LICENSE) [![Chat](https://img.shields.io/badge/chat-discord-blue.svg?style=flat-square)](https://discord.gg/NQRpmr2)
**Gnar** is an open-source Discord bot written in Java and Kotlin, using JDA 3 and Lavaplayer.
Gnar provides various features including music playback, fun and moderation commands.

## Self-host Gnar-bot
- **Notice:** We do not provide support for user-hosted versions of Gnar, though we do not disallow it.
- **Notice:** We are not responsible for anything that this work does to you or your server and can not be held liable for anything pertaining to it. 
- **Notice:** Gnar is a sharded bot and is made for thousands of servers, not just one. Within the bot.conf file located in /data/, there is a `shards` option. If you plan to only have the bot be on less than 100 servers, setting it to 1 should be fine. If you are planning on more, then your shard count should go with how we do ours {guild count}\700=Number of shards.

**ONLY EXPERIENCED DEVELOPERS SHOULD MODIFY / USE THE BOT**

- **Step 1:** You are going to want to fork the repo and compile the bot. This step is by far the most necessary one.
- **Step 2:** Inside of the /data/ folder, you will see a credentials.conf.example file, this will lay out how you set up Gnar's credentials for his many API's and his bot token. Replace each one with the necessary key needed for the supplied section.
- **Step 3:** Once this is done, you can run Gnar by grabbing his compiled .jar file and running `java -jar GnarBotCompiledJarName.jar`.

## Main Contributors
* Avarel
* Xevryll
* Gatt

## Dependencies
This project uses **Java 8**.

* [JDA 3](https://github.com/DV8FromTheWorld/JDA)
* [LavaPlayer](https://github.com/sedmelluq/lavaplayer)
* [Pippo Framework](https://github.com/decebals/pippo)
* [Kotlin](https://kotlinlang.org/)
* [Guice](https://github.com/google/guice)
* [Unirest](https://github.com/Mashape/unirest-java)
