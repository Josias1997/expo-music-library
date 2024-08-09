import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';

import { ExpoMusicLibraryViewProps } from './ExpoMusicLibrary.types';

const NativeView: React.ComponentType<ExpoMusicLibraryViewProps> =
  requireNativeViewManager('ExpoMusicLibrary');

export default function ExpoMusicLibraryView(props: ExpoMusicLibraryViewProps) {
  return <NativeView {...props} />;
}
