import React, { useRef } from 'react';
import { StyleSheet, View, ActivityIndicator } from 'react-native';
import { WebView } from 'react-native-webview';
import config from '../config';

const KioskWebView = () => {
  const webViewRef = useRef<WebView>(null);

  const handleError = () => {
    // Reload on error
    setTimeout(() => {
      webViewRef.current?.reload();
    }, 5000);
  };

  return (
    <View style={styles.container}>
      <WebView
        ref={webViewRef}
        source={{ uri: config.kiosk_url }}
        style={styles.webview}
        onError={handleError}
        startInLoadingState={true}
        renderLoading={() => (
          <ActivityIndicator
            color="#ffffff"
            size="large"
            style={styles.loading}
          />
        )}
        javaScriptEnabled={true}
        domStorageEnabled={true}
        mediaPlaybackRequiresUserAction={false}
        allowsFullscreenVideo={true}
        userAgent="Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  webview: {
    flex: 1,
  },
  loading: {
    position: 'absolute',
    top: '50%',
    left: '50%',
  },
});

export default KioskWebView;
