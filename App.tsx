import React, { useEffect, useRef } from 'react';
import { SafeAreaView, StatusBar, StyleSheet, BackHandler } from 'react-native';
import KeepAwake from 'react-native-keep-awake';
import KioskWebView from './src/components/KioskWebView';
import { NativeModules } from 'react-native';

const { KioskModule } = NativeModules;

const App = () => {
  const backPressCount = useRef(0);
  const backPressTimeout = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    // Enable kiosk mode
    KioskModule.enableKioskMode();

    // Prevent back button exit (unless pressed 5 times)
    const backHandler = BackHandler.addEventListener(
      'hardwareBackPress',
      () => {
        backPressCount.current += 1;

        // Clear existing timeout
        if (backPressTimeout.current) {
          clearTimeout(backPressTimeout.current);
        }

        // If pressed 5 times, allow exit
        if (backPressCount.current >= 5) {
          backPressCount.current = 0;
          return false; // Allow exit
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
        <KioskWebView />
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
