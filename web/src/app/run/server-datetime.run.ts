// services
import { ServerDatetimeService } from '../angular-ids-project/src/components/server-datetime/server-datetime.service';

/** @ngInject */
export function ServerDateTimeRun(serverDatetimeService: ServerDatetimeService): void {
  serverDatetimeService.initialize('util/current-datetime', 600000);
}
