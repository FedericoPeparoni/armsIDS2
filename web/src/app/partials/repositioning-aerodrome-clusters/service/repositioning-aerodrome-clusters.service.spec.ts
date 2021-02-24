// interface
import { IRepositioningAerodromeCluster } from '../repositioning-aerodrome-clusters.interface';

// service
import { RepositioningAerodromeClustersService, endpoint } from './repositioning-aerodrome-clusters.service';

describe('service RepositioningAerodromeClustersService', () => {

    let httpBackend,
        restangular;

    let repositioningCluster: IRepositioningAerodromeCluster = {
        id: null,
        repositioning_aerodrome_cluster_name: 'Test Cluster',
        enroute_fees_are_exempt: 0,
        approach_fees_are_exempt: 0,
        aerodrome_fees_are_exempt: 0,
        late_arrival_fees_are_exempt: 0,
        late_departure_fees_are_exempt: 0,
        parking_fees_are_exempt: 0,
        international_pax: 0,
        domestic_pax: 0,
        extended_hours: 0,
        aerodrome_identifiers: ['Test Aerodrome'],
        flight_notes: 'Test Notes'
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

    it('should be registered', inject((repositioningAerodromeClustersService: RepositioningAerodromeClustersService) => {
        expect(repositioningAerodromeClustersService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of repositioning aerodrome clusters', inject((repositioningAerodromeClustersService: RepositioningAerodromeClustersService) => {

            let expectedResponse = Array<IRepositioningAerodromeCluster>();

            repositioningAerodromeClustersService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create a single repositioning aerodrome cluster', inject((repositioningAerodromeClustersService: RepositioningAerodromeClustersService) => {

            repositioningAerodromeClustersService.create(repositioningCluster);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, repositioningCluster);

            httpBackend.flush();
        }));
    });

    describe('update', () => {
        it('should update a single repositioning aerodrome cluster', inject((repositioningAerodromeClustersService: RepositioningAerodromeClustersService) => {

            repositioningCluster.id = 1;

            repositioningAerodromeClustersService.update(repositioningCluster, repositioningCluster.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${repositioningCluster.id}`)
                .respond(200, repositioningCluster);

            httpBackend.flush();
        }));
    });

    describe('delete', () => {
        it('should delete a single repositioning aerodrome cluster', inject((repositioningAerodromeClustersService: RepositioningAerodromeClustersService) => {

            repositioningCluster.id = 1;

            repositioningAerodromeClustersService.delete(repositioningCluster.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${repositioningCluster.id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });
});
