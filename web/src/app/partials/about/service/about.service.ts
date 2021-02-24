import { ConfigService } from '../../../angular-ids-project/src/components/services/config/config.service';

export class AboutService {

  private service: ConfigService;

  /** @ngInject */
  constructor(private configService: ConfigService) {
    this.service = configService;
  }

  public list(): Object {
    return this.service.list();
  }

}
