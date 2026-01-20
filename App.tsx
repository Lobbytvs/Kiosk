import React, { useEffect, useRef, useState } from 'react';
import { SafeAreaView, StatusBar, StyleSheet, BackHandler } from 'react-native';
import KeepAwake from 'react-native-keep-awake';
import AsyncStorage from '@react-native-async-storage/async-storage';
import KioskWebView from './src/components/KioskWebView';
import SideMenu from './src/components/SideMenu';
import SettingsScreen from './src/components/SettingsScreen';
import { NativeModules } from 'react-native';
import config from './src/config';

const { KioskModule } = NativeModules;

const App = () => {
  const [kioskUrl, setKioskUrl] = useState(config.kiosk_url);
  const [showMenu, setShowMenu] = useState(false);
  const [showSettings, setShowSettings] = useState(false);
  const backPressCount = useRef(0);
  const backPressTimeout = useRef<NodeJS.Timeout | null>(null);

  // Load saved URL on app start
  useEffect(() => {
    const loadSavedUrl = async () => {
      try {
        const savedUrl = await AsyncStorage.getItem('kiosk_url');
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
      () => {
        // If menu or settings is open, close them
        if (showMenu) {
          setShowMenu(false);
          return true;
        }
        if (showSettings) {
          setShowSettings(false);
          return true;
        }

        backPressCount.current += 1;

        // Clear existing timeout
        if (backPressTimeout.current) {
          clearTimeout(backPressTimeout.current);
        }

        // If pressed 5 times, open menu
        if (backPressCount.current >= 5) {
          backPressCount.current = 0;
          setShowMenu(true);
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
  }, [showMenu, showSettings]);

  const handleUrlSaved = (newUrl: string) => {
    setKioskUrl(newUrl);
    setShowSettings(false);
  };

  const handleExitApp = () => {
    BackHandler.exitApp();
  };

  // Show settings screen
  if (showSettings) {
    return (
      <>
        <StatusBar hidden={true} />
        <KeepAwake />
        <SafeAreaView style={styles.container}>
          <SettingsScreen
            currentUrl={kioskUrl}
            onClose={() => setShowSettings(false)}
            onUrlSaved={handleUrlSaved}
          />
        </SafeAreaView>
      </>
    );
  }

  return (
    <>
      <StatusBar hidden={true} />
      <KeepAwake />
      <SafeAreaView style={styles.container}>
        <KioskWebView url={kioskUrl} />
        <SideMenu
          visible={showMenu}
          onClose={() => setShowMenu(false)}
          onOpenSettings={() => setShowSettings(true)}
          onExitApp={handleExitApp}
        />
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
