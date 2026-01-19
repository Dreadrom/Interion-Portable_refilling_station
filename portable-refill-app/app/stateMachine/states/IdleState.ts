import { BaseState } from '../BaseState';
import { StateMachine } from '../StateMachine';

export class IdleState extends BaseState {
  name = 'IdleState';

  constructor(machine: StateMachine) {
    super(machine);
  }

  onEnter() {
    console.log('Idle: waiting for pump selection');
  }
}
