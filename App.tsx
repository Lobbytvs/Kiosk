import React, { useEffect } from 'react';
import { SafeAreaView, StatusBar, StyleSheet, BackHandler } from 'react-native';
import KeepAwake from 'react-native-keep-awake';
import KioskWebView from './src/components/KioskWebView';
import { NativeModules } from 'react-native';

const { KioskModule } = NativeModules;

const App = () => {
  useEffect(() => {
    // Enable kiosk mode
    KioskModule.enableKioskMode();

    // Prevent back button exit
    const backHandler = BackHandler.addEventListener(
      'hardwareBackPress',
      () => true // Return true to prevent default behavior
    );

    return () => backHandler.remove();
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
