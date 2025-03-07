# PaddlePro - Event Management System

## Firebase Configuration

To set up Firebase Analytics in the application:

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select an existing one
3. Navigate to Project Settings > Service Accounts
4. Click "Generate New Private Key"
5. Download the JSON credentials file
6. In the application:
   - Go to API Management > Analytics APIs
   - Click "Configure Firebase"
   - Select the downloaded credentials JSON file
   - The application will automatically configure Firebase

### Features

- Real-time weather data using Open-Meteo API (free)
- Location search using OpenStreetMap (free)
- Event analytics through Firebase
- User behavior tracking
- Attendance patterns analysis

### Analytics Events Tracked

- App opens
- Screen views
- Event creations
- User registrations
- Venue bookings
- Search queries

## Note

The Firebase credentials file contains sensitive information. Never commit it to version control.
Add `firebase-credentials.json` to your `.gitignore` file. 