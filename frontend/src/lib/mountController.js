import { Shared } from "@/lib/shared.js";
export class MountController extends Shared {
  constructor() {
    super();
    this.target = null;
    this.initializer = null;
    this.initialized = false;
    this.mounted = false;
    this.controlled = false;
  }
  reset() {
    this.id = 0;
    this.onMount = null;
    this.onConclude = null;
    this.obj = null;
    this.target = null;
    this.initializer = null;
    this.initialized = false;
    this.mounted = false;
    this.controlled = false
  }
  prvInitialize(target) {
    this.target = target;
    this.initialized = true;
    this.initializer(target);
  }
  initialize(id) {
    this.initialized = false;
    this.id = id;
  }
  initializeWithArgs(id, obj) {
    this.initialized = false;
    this.id = id;
    this.obj = obj;
  }
  setUp(target, initializer, onConclude) {
    this.initializer = initializer;
    this.onConclude = onConclude;
    this.controlled = true;
    if (!this.initialized && this.mounted) {
      this.prvInitialize(target);
    }
  }
  mount(target) {
    this.mounted = true;
    if (this.controlled && !this.initialized) this.prvInitialize(target);
  }
}
export class DialogController extends MountController {
  constructor() {
    super();
  }
  setUp(target, initializer, onConclude) {
    target.isDialog = true;
    this.initialized = false;
    super.setUp(target, initializer, onConclude);
  }
}
