import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AlarmService {

  private alarmSound = new Audio('sounds/alarm.mp3');
  private isPlaying = false;

  constructor() {
    this.alarmSound.loop = true;
  }

  start(): void {
    if (this.isPlaying) return;

    this.alarmSound.play().catch(err => {
      console.warn('Audio play blocked. User interaction required.', err);
    });

    this.isPlaying = true;
  }

  stop(): void {
    if (!this.isPlaying) return;

    this.alarmSound.pause();
    this.alarmSound.currentTime = 0;
    this.isPlaying = false;
  }

  isAlarmPlaying(): boolean {
    return this.isPlaying;
  }
}
