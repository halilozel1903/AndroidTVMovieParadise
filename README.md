# Movie 🎬 Paradise 🎥

![Screenshot](https://github.com/halilozel1903/AndroidTVMovieParadise/blob/master/screenshots/androidtv.png)

Android TV is a version of the Android operating system. It's developed by Google for soundbars, set-top boxes, digital media players, and TVs with native applications. It's a replacement for Google TV. Android TV platform was first launched in June 2014. This platform has also been adopted as smart TV middleware by a company such as Sharp and Sony.

**Project Objective**: This app is designed to provide users with a seamless movie discovery and streaming experience on the big screen.

Movie Paradise is an Android TV 📺 app. This app is working Android TV & Android STB. 📹 🎞

The application uses **The Movie Database** API to fetch movie data: 👇🏻

https://www.themoviedb.org/documentation/api

The application uses the basic components of Android TV application.

Build apps that let users experience your app's immersive content on the big screen. Users can discover your content recommendations on the home screen, and the leanback library provides APIs to help you build a great use experience for a remote control.

I wrote a blog post about Android TV. You can access the article from the link below.

https://medium.com/@halilozel1903/how-to-develop-android-tv-app-5e251f3aa56b

## Features

### Home 🏡 Screen 📺

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/home.png)

This is the area where the data taken from API is displayed.

The search icon is on the main page.

By clicking on the icon, you can search.

### Categories
The app organizes movies into four categories to make browsing easier:
- **Now Playing**: Movies currently in theaters.
- **Top Rated**: Highest-rated movies based on user reviews.
- **Popular**: Trending movies with the most views.
- **Upcoming**: Future releases for the movie buffs to look forward to.

### Now Playing ▶️

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/nowplaying_focus.png)

### Top ✍🏻 Rated 🔝

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/toprated.png)

### Popular 🥳

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/popular.png)


### Upcoming 🔜

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/upcoming.png)


### Detail 📜 Screen ✅

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/detail.png)

When a movie is selected, a detailed screen appears showing:
- **Title**
- **Poster**
- **Labels** (ex. Genre, Release Date)
- **Director's name**
- Brief **overview** of the plot

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/detail_area.png)


### Detail Screen Recommend 🎁

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/detail_recommend.png)


### Detail Screen Cast 🙎🏼‍♀️ 👨

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/detail_cast.png)


### Search Screen 🔎

The search icon is placed directly on the home screen. Users can search for movies by clicking on the search icon, typing the movie name, and getting instant results. This feature makes it easy to quickly find your favorite movies.

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/search.png)

Search results are displayed instantly.

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/search_result.png)

We searched the SpiderMan movie. You can view the detail of the movie.

![Screenshot](https://github.com/halilozel1903/MovieParadise/blob/master/screenshots/search_result_detail.png)

## How to Build the App
Using standard Android TV components and libraries, follow these steps to build the app locally:

1.  Clone the repository:
```shell
git clone https://github.com/your-repository-url/AndroidTVMovieParadise.git
```
2.  Open the project in Android Studio.
3.  Sync Gradle files.
4.  Get an API key from [The Movie Database] (https://www.themoviedb.org/documentation/api). 
5.  Add the API key to the project.
6.  Build and run the app on an Android TV device.

## Warnings
**SAFETY WARNING: No Built-In Parental Controls**

Note, this app does not have built-in parental control features. This can be a concern for families, as younger viewers may be able to access content that is rated for mature or other audiences.

**Lack of Streaming Capabilities**

Note, this app currently does not support in-app streaming directly. Rather, this app is meant to provide movie information and facilitate a better movie-picking experience. Please use another streaming service in order to watch your desired movies.

**Wi-Fi Connection Necessary**

Note, The Movie Database API requires an active internet connection to properly function. Users without a proper Wi-Fi connection are subject to frequent interruptions and limited functionality.


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

