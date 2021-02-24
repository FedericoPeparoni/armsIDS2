// services
import { AerodromeOperationalHoursService, endpoint } from './aerodrome-operational-hours.service';

// interface
import { IAerodromeOperationalHours } from '../aerodrome-operational-hours.interface';

describe('service AerodromeOperationalHoursService', () => {

    let httpBackend, restangular;

    const aerodromeOperationalHours: IAerodromeOperationalHours = {
        aerodrome: {
            id: 1,
            aerodrome_name: 'AAAA',
            extended_aerodrome_name: null,
            aixm_flag: true,
            geometry: {
                type: 'Point',
                coordinates: [
                    55.5, 60.5
                ]
            },
            is_default_billing_center: false,
            billing_center: null,
            aerodrome_category: null,
            external_accounting_system_identifier: null,
            aerodrome_services: []
        },
        operational_hours_monday: '0000-0100;1500-1900',
        operational_hours_tuesday: '0000-0100;1500-1900',
        operational_hours_wednesday: '0000-0100;1500-1900',
        operational_hours_thursday: '0000-0100;1500-1900',
        operational_hours_friday: '0000-0100;1500-1900',
        operational_hours_saturday: '0000-0100;1500-1900',
        operational_hours_sunday: '0000-0100;1500-1900',
        operational_hours_holidays1: '0000-0100;1500-1900',
        operational_hours_holidays2: '0000-0100;1500-1900',
        holiday_dates_holidays1: '01/01;05/09',
        holiday_dates_holidays2: '10/19;03/08'
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

    it('should be registered', inject((aerodromeOperationalHoursService: AerodromeOperationalHoursService) => {
        expect(aerodromeOperationalHoursService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of aerodrome operational hours', inject((aerodromeOperationalHoursService: AerodromeOperationalHoursService) => {

            let expectedResponse = Array<IAerodromeOperationalHours>();
            aerodromeOperationalHoursService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create an aerodrome operational hours', inject((aerodromeOperationalHoursService: AerodromeOperationalHoursService) => {

            aerodromeOperationalHoursService.create(aerodromeOperationalHours);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, aerodromeOperationalHours);

            httpBackend.flush();
        }));
    });

    describe('update', () => {
        it('should update an single aerodrome operational hours', inject((aerodromeOperationalHoursService: AerodromeOperationalHoursService) => {

            aerodromeOperationalHours.id = 1;
            aerodromeOperationalHoursService.update(aerodromeOperationalHours, aerodromeOperationalHours.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${aerodromeOperationalHours.id}`)
                .respond(200, aerodromeOperationalHours);

            httpBackend.flush();
        }));
    });

    describe('delete', () => {
        it('should delete a single aerodrome operational hours', inject((aerodromeOperationalHoursService: AerodromeOperationalHoursService) => {

            aerodromeOperationalHours.id = 1;
            aerodromeOperationalHoursService.delete(aerodromeOperationalHours.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${aerodromeOperationalHours.id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });
});
