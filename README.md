# GOD OF WATCH

Android companion app prototype for Amazfit GTS 2.

## Current scope
- Dark, gold-accented companion UI
- Watch-face gallery and editor preview foundation
- Bluetooth permission and device discovery groundwork
- GitHub Actions debug APK build

## Important GTS 2 compatibility note
Amazfit GTS 2 uses Zepp's device ecosystem and watch-face transfer formats/protocols. Android Bluetooth discovery alone does not install a watch face. Direct transfer will be implemented only after validating the GTS 2 protocol and compatible face package format on a real device. Until then, the app must not claim a face was installed.

## Build
GitHub Actions runs Gradle assembleDebug on pushes and pull requests. The debug APK is uploaded as an Actions artifact.


## Currency quotes prototype
- Watch screen includes USD/RUB, EUR/RUB, GBP/RUB, BTC/USD, USD/EUR and EUR/USD.
- The companion app polls the selected quote sources every five minutes while the screen is active and displays source timestamps and stale/error states.
- Foreign-exchange data uses exchangerate.dev's anonymous endpoint. Its free/keyless feed is delayed by 15 minutes and is indicative only; five-minute polling does not mean five-minute-fresh market data. BTC/USD uses CoinGecko's public simple-price endpoint, subject to its public rate limits and cache.
- The current implementation displays quotes in the phone app. It does not yet transmit dynamic values to the GTS 2 watch face. That requires validating a supported dynamic watch-face/data-transfer mechanism on the actual device.
