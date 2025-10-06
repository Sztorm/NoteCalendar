<p align="center">
  <img src="./assets/icon.png" width="150px">
</p>
<h1 align="center">NoteCalendar</h1>

*Note Calendar* is a simple offline app which allows to manage notes for different days and offers a
quick way to reach each of them.

Minimum Android version: 5.0 (Lollipop, API level 21)

[<img alt="Get it on IzzyOnDroid" height="80" src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png">](https://apt.izzysoft.de/fdroid/index/apk/com.sztorm.notecalendar)
[<img alt="Get it on Google Play" height="80" src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png">](https://play.google.com/store/apps/details?id=com.sztorm.notecalendar)

## Current state

I'm actively migrating xml views to Jetpack Compose, which can be observed on
[compose-migration](https://github.com/Sztorm/NoteCalendar/tree/compose-migration) branch.

Jetpack Compose will help me with app maintenance as it's newer, and it doesn't require that much
Android compatibility code, and the code itself tends to be shorter and easier to reason about.

That will also allow me to add more features soon, but now the goal is to migrate whole codebase
without too much meddling with the current app design.

### Current Progress:
 * Day view - 90% complete
 * Week view - 80% complete (differs significantly from current one)
 * Month view - 90% complete
 * Settings views - 0% complete
 * Activity view - 0% complete

## Screenshots

<img src="assets/01.png" alt="app screenshot 01" width=200 height=411> <img src="assets/02.png" alt="app screenshot 02" width=200 height=411> <img src="assets/03.png" alt="app screenshot 03" width=200 height=411> <img src="assets/04.png" alt="app screenshot 04" width=200 height=411>

<img src="assets/05.png" alt="app screenshot 05" width=200 height=411> <img src="assets/06.png" alt="app screenshot 06" width=200 height=411> <img src="assets/07.png" alt="app screenshot 07" width=200 height=411> <img src="assets/08.png" alt="app screenshot 08" width=200 height=411>

<img src="assets/09.png" alt="app screenshot 09" width=200 height=411> <img src="assets/10.png" alt="app screenshot 10" width=200 height=411>

## Features

* Day view
  * This view is used to add, edit or delete note
* Week view
* Month view
  * Days that constains a note are marked with a ring
  * Day that is currently selected is marked with a solid circle
  * Today's day is marked with a different color of text
  * Long press on a day's number allow to quickly add or edit note for that day
* Settings view
  * Theming
    * Setting custom theme which includes 10 modifiable colors
    * Setting light theme
    * Setting dark theme
    * Setting default theme based on system settings
  * Notes deletion
  * Notifications
    * When turned on, a notification will appear at notification time
  * Setting first day of week
  * Setting starting view

## Translations
<a href="https://hosted.weblate.org/engage/note-calendar/">
<img src="https://hosted.weblate.org/widget/note-calendar/multi-auto.svg" alt="Translations state" />
</a>

When English translation string is `?` that means the translation is optional and will be
translated automatically. That way specific strings are translated for all languages without manual
translations.
You can however add translation for your language for that type of string if the automatic one is
not the best fit.

You can help with translations on https://hosted.weblate.org/projects/note-calendar/.

Accessible translation platform is available thanks to Weblate and their support for libre projects. Hosting such platform costs money, if you want to help the Weblate project, you can donate [here](https://weblate.org/pl/donate/).

## License

*NoteCalendar* is licensed under the MIT license.

[More about license](LICENSE)

[Privacy Policy](PRIVACY-POLICY.md)