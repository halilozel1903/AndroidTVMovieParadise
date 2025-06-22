# Movie 🎬 Paradise 🎥

![Screenshot](https://github.com/halilozel1903/AndroidTVMovieParadise/blob/master/screenshots/androidtv.png)

Android TV is a version of the Android operating system. It's developed by Google for soundbars, set-top boxes, digital media players, and TVs with native applications. It's a replacement for Google TV. Android TV platform was first launched in June 2014. This platform has also been adopted as smart TV middleware by a company such as Sharp and Sony.


Movie Paradise is an Android TV 📺 app. This app is working Android TV & Android STB. 📹 🎞

Application is using The Movie Database. 👇🏻

https://www.themoviedb.org/documentation/api

The application uses the basic components of Android TV application.

Build apps that let users experience your app's immersive content on the big screen. Users can discover your content recommendations on the home screen, and the leanback library provides APIs to help you build a great use experience for a remote control.

I wrote a blog post about Android TV. You can access the article from the link below.

https://medium.com/@halilozel1903/how-to-develop-android-tv-app-5e251f3aa56b

## Technologies Used 🛠️

- ☕ **Java** – Main programming language
- 📐 **AndroidX & Material Components**
- 📺 **Leanback** library for TV UI components
- 🌐 **Retrofit** and **OkHttp** for network requests
- ♻️ **RxJava** and **RxAndroid** for reactive programming
- 🖼️ **Glide** for image loading
- 🛠️ **Dagger** for dependency injection
- 🗄️ **SQLBrite** for local database handling
- 📺 **YoutubeTv** for video playback

## Building

This project relies on the **YoutubeTvView** library, which is hosted on
**JCenter**. If Gradle cannot resolve this dependency, ensure that the
`jcenter()` repository is included in your Gradle configuration. The provided
`build.gradle` already contains this entry.

## Home 🏡 Screen 📺

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/home.png)

This is the area where the data retrieved from the API is displayed. The search
icon sits right on the main page, letting you quickly find a movie. The home
screen is organized into four categories: **Now Playing**, **Top Rated**,
**Popular** and **Upcoming**.

## Now Playing ▶️

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/nowplaying_focus.png)

## Top ✍🏻 Rated 🔝

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/toprated.png)

## Popular 🥳

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/popular.png)


## Upcoming 🔜

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/upcoming.png)


## Detail 📜 Screen ✅

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/detail.png)

The detail page provides extensive information about a movie, including its
title, poster, labels, director and overview.

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/detail_area.png)


## Detail Screen Recommend 🎁

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/detail_recommend.png)


## Detail Screen Cast 🙎🏼‍♀️ 👨

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/detail_cast.png)


## Search Screen 🔎

You can search for any movie on the dedicated search screen, and results appear
instantly. In the example below we looked up the Spider-Man movie and opened its
details.

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/search.png)

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/search_result.png)

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/search_result_detail.png)

## Sources 📚

- [How to develop Android TV App?](https://halilozel1903.medium.com/how-to-develop-android-tv-app-5e251f3aa56b)
- [Android TV](https://developer.android.com/tv/) <br>
- [Building an Android TV app](https://medium.com/@Marcus_fNk/building-an-android-tv-app-part-1-7f59b3747446)<br>

## Donation 💸

If this project help you 💁 , you can give me a cup of coffee. ☕

[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/halilozel1903)

## License ℹ️
```
MIT License

Copyright (c) 2023 Halil OZEL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

