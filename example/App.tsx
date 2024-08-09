import { StyleSheet, Text, View } from 'react-native';

import * as ExpoMusicLibrary from 'expo-music-library';

export default function App() {
  return (
    <View style={styles.container}>
      <Text>{ExpoMusicLibrary.hello()}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
