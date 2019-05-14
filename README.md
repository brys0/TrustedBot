# TrustedBot
The TrustedBot is a bot for Discord written in Java using [JDA](https://github.com/DV8FromTheWorld/JDA), [JDA-Utilities](https://github.com/JDA-Applications/JDA-Utilities) and [lavaplayer](https://github.com/sedmelluq/lavaplayer).
## Features
 - Musicplayer (YouTube, Soundcloud, Files via HTTP and more)
 - Image Commands (Nekos, Lizards, Cats, Dogs etc.)
 - Rainbow Six Siege Statistics
 - Twitch Notifications
 - Reddit Grabber (Send posts from Subreddits like /r/animemes into a Channel)
 - Credit-System (which is currently pretty useless)
 - Custom Textcommands
 - Aliases to create commands triggering another command with specific arguments
 - Some more commands with various features like UrbanDictionary or NumberFacts

## Setup
1. Clone the repository.
2. Build the Bot using maven.
3. Start the Bot for the first time to create the default config. It will shutdown right after creating it.
4. Fill out the config.yml, everything should be commented.
5. Launch the Bot again.

## Commands
The default prefix for commands is `!`.

| Command         | Alias                | Usable by     | Description                                                          | Example                                            |
| --------------- | -------------------- | ------------- | -------------------------------------------------------------------- | -------------------------------------------------- |
| AliasAdd        | -                    | Administrator | Creates a command alias                                              | !aliasadd radio play https://radio-url.tld
| Aliases         | -                    | Everyone      | Lists all aliases                                                    | !aliases
| AliasRemove     | -                    | Administrator | Removes a command alias                                              | !aliasremove radio 
| Coinflip        | cf                   | Everyone      | Flip a coin and win or lose credits                                  | !cf heads 100
| Color           | colors, colorchooser | Everyone      | Choose a color for your name using reactions                         | !color
| Credits         | -                    | Everyone      | Show the amount of someone's credits                                 | !credits @Pheromir#1337
| Daily           | -                    | Everyone      | Claim a daily reward                                                 | !daily
| DjAdd           | -                    | Administrator | Give someone DJ permissions                                          | !djadd @Pheromir#1337
| DjRemove        | -                    | Administrator | Remove someone's DJ permissions                                      | !djremove @Pheromir#1337
| Export          | -                    | Everyone      | Export the current playlist to haste-/pastebin                       | !export
| ExtraAdd        | -                    | Bot-Admin     | Allow the specified user extra permissions (!volume / titles longer than 2 hours) | !extraadd @Pheromir#1337
| ExtraRemove     | -                    | Bot-Admin     | Remove extra permissions of someone                                  | !extraremove @Pheromir#1337
| Forward         | -                    | DJ            | Fast-Forward the current track                                       | !forward 1:45
| Google          | g                    | Everyone      | Send a google search URL                                             | !g Discord
| Import          | -                    | Everyone      | Import a playlist from haste-/pastebin                               | !import https://hastebin.com/pasteId
| NumberFact      | nf                   | Everyone      | Show a random fact about the given number (integers only)            | !nf 666
| Play            | -                    | Everyone      | Add a track to the playlist                                          | !play never gonna give you up / !play https://www.youtube.com/watch?v=dQw4w9WgXcQ
| Playing         | np                   | Everyone      | Shows the currently playing track                                    | !np
| Prefix          | präfix               | Administrator | Change the command-prefix                                            | !prefix ~
| Queue           | q, playlist          | Everyone      | Shows the playlist (max 10 tracks), !q clear to clear the Queue, !q repeat to repeat it | !q
| R6              | -                    | Everyone      | Show the Rainbow Six Siege statistics of the specified user on uplay | !r6 TRST.Pheromir
| Reddit          | -                    | Administrator | List/Enable/Disable the receiving of Reddit posts for the current channel | !reddit animemes new
| Rewind          | -                    | DJ            | Rewind the current track                                             | !rewind 1:34
| Seek            | -                    | DJ            | Set the playback time of the current track                           | !seek 4:04
| SetCredits      | -                    | Administrator | Set the credits of a user                                            | !setcredits @Pheromir#1337 666
| Skip            | -                    | DJ            | Skip the current track or specify a track of the queue               | !skip / !skip 4
| Stats           | -                    | Bot-Admin     | Show statistics of the Bot (Uptime, RAM-Usage)                       | !stats
| Status          | -                    | Bot-Admin     | Change the status of the Bot (is playing..)                          | !status play Minecraft
| Stop            | -                    | DJ            | Stop the playback and clear the queue                                | !stop
| TextCmdAdd      | -                    | Administrator | Create a new text command                                            | !textcmdadd rules 1. Be nice! [...]
| TextCmdRemove   | -                    | Administrator | Remove a text command                                                | !textcmdremove rules
| TextCmds        | -                    | Everyone      | List all existing text commands                                      | !textcmds
| Toggle          | -                    | Administrator | Enable/Disable a command on the guild                                | !toggle play
| Twitch          | -                    | Administrator | Enable/Disable Twitch notifications for a twitch-user in a channel   | !twitch Rainbow6
| UrbanDictionary | ud                   | Everyone      | Show the top 5 definitions from the given keyword                    | !ud weeb
| Volume          | vol                  | Extra         | Set the playback volume                                              | !vol 10

### Image Commands
Image commands are usable by everyone, but commands marked with a `*` are only usable in NSFW-channels

| Command     | Description                            |
| ----------- | -------------------------------------- |
| Cat         | Shows a random cat picture
| Cuddle      | Shows a random cuddle gif
| Dog         | Shows a random dog picture
| `*`EroKemo  | Shows a random erotic kemonomimi image
| Goose       | Shows a random goose picture
| Hug         | Shows a random hug gif
| Kemo        | Shows a random kemonomimi image
| Kiss        | Shows a random kiss gif
| `*`Lewd     | Shows a random lewd neko image
| `*`LewdGif  | Shows a random lewd neko gif
| `*`LewdKemo | Shows a random lewd kemonomimi image
| `*`LewdYuri | Shows a random lewd yuri image
| Lizard      | Shows a random lizard picture
| `*`Loli     | Shows a random not-really-loli image
| Neko        | Shows a random neko image
| NekoGif     | Shows a random neko gif
| Pat         | Shows a random pat gif
| Poke        | Shows a random poke gif
| Tickle      | Shows a random gif
| `*`Yuri     | Shows a random image





