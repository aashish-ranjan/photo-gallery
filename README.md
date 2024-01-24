# Photo-Gallery Android App

## Overview
The Photo-Gallery app fetches photos from the Flickr API and displays them in a Grid RecyclerView using DiffUtil. It offers a search feature in the action bar, allowing users to find specific photos. Clicking on a photo takes the user to a detailed view of the photo, which opens in a WebView. The app supports pagination for fetching photos and provides contextual error and loader states. Additionally, through background polling mechanism, the user is shown a notification whenever a new photo matching the user's last search query becomes available.

## Features
- **Search**: Users can search for specific photos using the action bar.
- **Photo Details**: Clicking on a photo opens its detailed view in a WebView.
- **Pagination**: Supports pagination for fetching photos.
- **Notifications**: Notifies users when a new photo-matching their last search is available.
- **Error & Loader States**: Provides contextual error and loader states during photo fetching.

## Libraries Used
- **Retrofit** with Moshi converter: For making network requests.
- **Paging 3**: For pagination support.
- **WorkManager**: To check for the availability of new photos in the background state.
- **DataStore**: To save the user's last search query.
- **Coil**: For image loading.
- **Navigation Component**: For fragment navigation and the SafeArgs plugin.
