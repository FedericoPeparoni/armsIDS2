// interface
import { IRejectedItem } from '../rejected-items.interface';

// service
import { RejectedItemsService, endpoint } from './rejected-items.service';

describe('service RejectedItemsService', () => {

  let httpBackend,
      restangular;

  let rejectedItem: IRejectedItem = {
    record_type: 'ATS_MOV',
    rejected_date_time: '2017-03-25T21:02:33Z',
    error_message: 'Data not valid',
    error_details: 'AtcMovementLog.flightId:may not be null; ',
    rejected_reason: 'The object is not validated',
    raw_text: '7-Mar-17,GCIVE,BRITISH AIRWAYS,UG863/UM998,,B744,BAW,FAOR,EGLL,RUDAS,1955,320,MNV,2038,320,BUGRO,2056,320,H,sch,I',
    header: 'dateOfContact,registration,,route,flightId,aircraftType,operatorIcaoCode,departureAerodrome,destinationAerodrome,firEntryPoint,firEntryTime,,firMidPoint,firMidTime,,firExitPoint,firExitTime,,wakeTurbulence,flightCategory',
    json_text: 'eyJpZCI6bnVsbCwiZGF0ZU9mQ2',
    status: 'uncorrected',
    originator: null,
    file_name: 'atc.txt',
    filter_by: null,
    filter_by_record_type: null,
    filter_by_date: null,
    filter_by_status: null,
    filter_by_originator: null,
    filter_by_file_name: null
};

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(() => {
      inject(($httpBackend: angular.IHttpBackendService, Restangular: restangular.IService) => {
          httpBackend = $httpBackend;
          restangular = Restangular;

          $httpBackend.when('GET', (url: string): any => {
            if ('http://localhost:8080/api/system-configurations/noauth') {
              return true;
            };
          }).respond('ok');
      });
  });

  it('should be registered', inject((rejectedItemsService: RejectedItemsService) => {
      expect(rejectedItemsService).not.toEqual(null);
  }));

  describe('list', () => {
        it('should return an array of rejected items', inject((rejectedItemsService: RejectedItemsService) => {

            let expectedResponse = Array<IRejectedItem>();

            rejectedItemsService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create a single rejected item', inject((rejectedItemsService: RejectedItemsService) => {

            rejectedItemsService.create(rejectedItem);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, rejectedItem);

            httpBackend.flush();
        }));
    });

    describe('update', () => {
        it('should update a single rejected item', inject((rejectedItemsService: RejectedItemsService) => {

            rejectedItem.id = 1;

            rejectedItemsService.update(rejectedItem, rejectedItem.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${rejectedItem.id}`)
                .respond(200, rejectedItem);

            httpBackend.flush();
        }));
    });

    describe('delete', () => {
        it('should delete a single rejected item', inject((rejectedItemsService: RejectedItemsService) => {

            rejectedItem.id = 1;

            rejectedItemsService.delete(rejectedItem.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${rejectedItem.id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });
});
