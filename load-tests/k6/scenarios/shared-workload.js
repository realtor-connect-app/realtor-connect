import { bootstrapRealtor } from '../lib/auth.js';

let context;

export function bootstrapContext() {
  if (!context) {
    context = bootstrapRealtor();
  }
  return context;
}

