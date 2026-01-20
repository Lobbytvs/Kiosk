import React, { useEffect, useRef, useState } from 'react';
import { SafeAreaView, StatusBar, StyleSheet, BackHandler } from 'react-native';
import KeepAwake from 'react-native-keep-awake';
import KioskWebView from './src/components/KioskWebView';
import { NativeModules } from 'react-native';
import config from './src/config';

const { KioskModule, NativeMenuModule } = NativeModules;

const App = () => {
  const [kioskUrl, setKioskUrl] = useState(config.kiosk_url);
  const backPressCount = useRef(0);
  const backPressTimeout = useRef<NodeJS.Timeout | null>(null);

  // Load saved URL on app start from native storage
  useEffect(() => {
    const loadSavedUrl = async () => {
      try {
        const savedUrl = await NativeMenuModule.getStoredUrl();
        if (savedUrl) {
          setKioskUrl(savedUrl);
        }
      } catch (error) {
        console.log('Error loading saved URL:', error);
      }
    };
    loadSavedUrl();
  }, []);

  useEffect(() => {
    // Enable kiosk mode
    KioskModule.enableKioskMode();

    // Back button handler - 5 presses to open menu
    const backHandler = BackHandler.addEventListener(
      'hardwareBackPress',
      async () => {
        backPressCount.current += 1;

        // Clear existing timeout
        if (backPressTimeout.current) {
          clearTimeout(backPressTimeout.current);
        }

        // If pressed 5 times, open native menu
        if (backPressCount.current >= 5) {
          backPressCount.current = 0;

          try {
            const result = await NativeMenuModule.showMenu();
            // If result is a URL (from settings), update the WebView
            if (result && result.startsWith('http')) {
              setKioskUrl(result);
            }
          } catch (error) {
            console.log('Menu error:', error);
          }

          return true;
        }

        // Reset counter after 3 seconds
        backPressTimeout.current = setTimeout(() => {
          backPressCount.current = 0;
        }, 3000);

        return true; // Prevent exit
      }
    );

    return () => {
      backHandler.remove();
      if (backPressTimeout.current) {
        clearTimeout(backPressTimeout.current);
      }
    };
  }, []);

  return (
    <>
      <StatusBar hidden={true} />
      <KeepAwake />
      <SafeAreaView style={styles.container}>
        <KioskWebView url={kioskUrl} />
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000000',
  },
});

export default App;
