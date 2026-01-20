import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Modal,
  Animated,
  Dimensions,
  BackHandler,
} from 'react-native';

interface SideMenuProps {
  visible: boolean;
  onClose: () => void;
  onOpenSettings: () => void;
  onExitApp: () => void;
}

const SideMenu: React.FC<SideMenuProps> = ({
  visible,
  onClose,
  onOpenSettings,
  onExitApp,
}) => {
  const slideAnim = React.useRef(new Animated.Value(-300)).current;

  React.useEffect(() => {
    if (visible) {
      Animated.spring(slideAnim, {
        toValue: 0,
        useNativeDriver: true,
        tension: 65,
        friction: 11,
      }).start();
    } else {
      Animated.spring(slideAnim, {
        toValue: -300,
        useNativeDriver: true,
        tension: 65,
        friction: 11,
      }).start();
    }
  }, [visible, slideAnim]);

  React.useEffect(() => {
    if (visible) {
      const backHandler = BackHandler.addEventListener(
        'hardwareBackPress',
        () => {
          onClose();
          return true;
        }
      );
      return () => backHandler.remove();
    }
  }, [visible, onClose]);

  if (!visible) return null;

  return (
    <Modal
      transparent
      visible={visible}
      animationType="none"
      onRequestClose={onClose}>
      <View style={styles.overlay}>
        <TouchableOpacity
          style={styles.backdrop}
          activeOpacity={1}
          onPress={onClose}
        />
        <Animated.View
          style={[
            styles.menu,
            { transform: [{ translateX: slideAnim }] },
          ]}>
          <View style={styles.header}>
            <Text style={styles.headerText}>Kiosk Menu</Text>
          </View>

          <TouchableOpacity
            style={styles.menuItem}
            onPress={() => {
              onClose();
              onOpenSettings();
            }}>
            <Text style={styles.menuItemText}>⚙️ Settings</Text>
            <Text style={styles.menuItemSubtext}>Change kiosk URL</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.menuItem} onPress={onExitApp}>
            <Text style={[styles.menuItemText, styles.exitText]}>
              🚪 Exit App
            </Text>
            <Text style={styles.menuItemSubtext}>Close the kiosk</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.closeButton} onPress={onClose}>
            <Text style={styles.closeButtonText}>✕ Close Menu</Text>
          </TouchableOpacity>
        </Animated.View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    flexDirection: 'row',
  },
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
  },
  menu: {
    width: 300,
    height: '100%',
    backgroundColor: '#1a1a1a',
    borderRightWidth: 1,
    borderRightColor: '#3a3a3a',
    shadowColor: '#000',
    shadowOffset: { width: 2, height: 0 },
    shadowOpacity: 0.3,
    shadowRadius: 10,
    elevation: 10,
  },
  header: {
    padding: 20,
    backgroundColor: '#2a2a2a',
    borderBottomWidth: 1,
    borderBottomColor: '#3a3a3a',
  },
  headerText: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
  },
  menuItem: {
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#2a2a2a',
  },
  menuItemText: {
    fontSize: 18,
    fontWeight: '600',
    color: '#fff',
    marginBottom: 5,
  },
  menuItemSubtext: {
    fontSize: 14,
    color: '#888',
  },
  exitText: {
    color: '#ff6b6b',
  },
  closeButton: {
    position: 'absolute',
    bottom: 30,
    left: 20,
    right: 20,
    padding: 15,
    backgroundColor: '#2a2a2a',
    borderRadius: 8,
    alignItems: 'center',
  },
  closeButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default SideMenu;
