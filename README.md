## WTF is this
1. Your personal AI pal to learn speaking a language.
2. You only need your OpenAI api token (can be set in Settings) and you are ready to go.
3. Please set the TTS engine to the German language in Settings, otherwise you will be listening german with {you_language} pronunciation
4. You can set any OpenAi model you like, and the needed temperature

<img src="https://github.com/Grigoriym/AiPal/assets/31949421/380503c1-c4cf-42f1-9d2d-88ef162bb90b"  width="300" height="600">


## How does it work?
1. The TTS is done with your phone default TTS-engine, so any settings are done there, not on my side. But you still need check it before using the app.
2. The STT is done with the help of a speech recognition tool [Vosk](https://alphacephei.com/vosk/). 
   So the SST is done on your device with a small 80 MB file which does all the work
   Now you can choose between the default one and Vosk
3. The OpenAI chat gpt is used right now.
4. Only German language is supported for now, though it is not a problem to add other possible languages.
    If Vosk supports a language, it can be done.

## Android or Vosk SST?
1. Android is not continuous (it is interrupted when you stop speaking), requires internet connection,
    from my experience is more accurate than Vosk (and it is understandable),
    can capitalize words (everything that is capitalized in German)
2. Vosk is continuous (you decide when to stop and when to start speaking), 
    does not require internet connection

## Roadmap
0. ~~Make it available to copy the chat card text. I believe I need to add a copy button to client messages as well.~~
1. Local history of conversations
2. Connecting to your own server (like running ollama on your machine and creating a simple intermediary to connect to)
3. Other AIs (like Claude, Mixtral, ...) (maybe, do not see the reason for now)
4. SST chooser (WIP)
5. Language Chooser (WIP)

## Helpful projects
Key-Value dataStore for Kmm: https://github.com/russhwolf/multiplatform-settings

https://github.com/aallam/openai-kotlin

https://github.com/alphacep/vosk-android-demo
https://alphacephei.com/vosk/android

https://github.com/ElishaAz/Sayboard

https://medium.com/@meytataliti/building-a-simple-chat-app-with-jetpack-compose-883a240592d4


https://github.com/lambiengcode/compose-chatgpt-kotlin-android-chatbot

https://github.com/sollarp/voice-assistant-chatgpt
https://github.com/AndraxDev/speak-gpt

https://github.com/Marc-JB/TextToSpeechKt?tab=readme-ov-file

https://github.com/paulotaylor/klaude
https://github.com/tddworks/openai-kotlin
https://github.com/JustAmalll/Translator_KMM

https://github.com/ndenicolais/SpeechAndText.git

https://stackoverflow.com/questions/38675829/how-to-create-releases-for-public-or-private-repository-in-github
