import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IAircraftType {
  id?: number;
  aircraft_type: string;
  aircraft_name: string;
  aircraft_image: string;
  manufacturer: string;
  maximum_takeoff_weight: number;
  wake_turbulence_category: IStaticType;
}

export interface IAircraftTypeMinimal {
  id?: number;
  aircraft_type: string;
}

export interface IAircraftTypeSpring extends ISpringPageableParams {
  content: Array<IAircraftType>;
}
