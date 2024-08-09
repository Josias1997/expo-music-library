import { NativeModulesProxy, EventEmitter, Subscription } from 'expo-modules-core';

// Import the native module. On web, it will be resolved to ExpoMusicLibrary.web.ts
// and on native platforms to ExpoMusicLibrary.ts
import ExpoMusicLibraryModule from './ExpoMusicLibraryModule';
import ExpoMusicLibraryView from './ExpoMusicLibraryView';
import { ChangeEventPayload, ExpoMusicLibraryViewProps } from './ExpoMusicLibrary.types';

// Get the native constant value.
export const PI = ExpoMusicLibraryModule.PI;

export function hello(): string {
  return ExpoMusicLibraryModule.hello();
}

export async function setValueAsync(value: string) {
  return await ExpoMusicLibraryModule.setValueAsync(value);
}

const emitter = new EventEmitter(ExpoMusicLibraryModule ?? NativeModulesProxy.ExpoMusicLibrary);

export function addChangeListener(listener: (event: ChangeEventPayload) => void): Subscription {
  return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export { ExpoMusicLibraryView, ExpoMusicLibraryViewProps, ChangeEventPayload };
