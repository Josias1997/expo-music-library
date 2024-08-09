import * as React from 'react';

import { ExpoMusicLibraryViewProps } from './ExpoMusicLibrary.types';

export default function ExpoMusicLibraryView(props: ExpoMusicLibraryViewProps) {
  return (
    <div>
      <span>{props.name}</span>
    </div>
  );
}
