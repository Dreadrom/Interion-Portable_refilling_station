import { BaseState } from './BaseState';
import { IdleState } from './states/IdleState';

export class StateMachine {
  currentState!: BaseState;
  onStateChanged?: (state: BaseState) => void;

  start() {
    this.transitionTo(new IdleState(this));
  }

  transitionTo(state: BaseState) {
    this.currentState?.onExit();
    this.currentState = state;
    this.currentState.onEnter();
    this.onStateChanged?.(state);
  }
}
