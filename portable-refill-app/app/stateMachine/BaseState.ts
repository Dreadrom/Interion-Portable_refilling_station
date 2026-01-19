import { StateMachine } from './StateMachine';

export abstract class BaseState {
  protected machine: StateMachine;

  /** Used by UI to know which "screen" to render */
  abstract name: string;

  constructor(machine: StateMachine) {
    this.machine = machine;
  }

  /** Android: onEnter() */
  onEnter(): void {
    // optional override
  }

  /** Android: onExit() */
  onExit(): void {
    // optional override
  }
}
