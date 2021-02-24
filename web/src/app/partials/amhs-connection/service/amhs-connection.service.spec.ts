// interface
import { IAMHSConnection } from '../amhs-connection.interface';

// service
import { AMHSConnectionService, endpoint } from './amhs-connection.service';

describe('service AMHSConnetionService', () => {

    let httpBackend,
        restangular;

    let amhsConnection: IAMHSConnection = {
        id: 1,
        active: true,
        descr: 'test connection',
        protocol: 'P3',
        rtse_checkpoint_size: 4,
        rtse_window_size: 3,
	max_conn: 3,
	ping_enabled: true,
	ping_delay: null,
	network_device: null,
        local_bind_authenticated: false,
        local_passwd: 'default',
	local_hostname: '',
	local_ipaddr: '',
        local_port: 103,
        local_tsap_addr: 'zzz',
        local_tsap_addr_is_hex: false,
        remote_hostname: 'remote_1',
        remote_ipaddr: null,
        remote_port: 103,
        remote_passwd: 'default',
        remote_bind_authenticated: false,
        remote_class_extended: true,
        remote_content_corr: true,
	remote_dl_exp_prohibit: true,
	remote_rcpt_reass_prohibit: true,
        remote_idle_time: 120,
        remote_internal_trace: true,
        remote_latest_delivery_time: true,
        remote_tsap_addr: 'yyy',
        remote_tsap_addr_is_hex: false
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

    it('should be registered', inject((amhsConnectionService: AMHSConnectionService) => {
        expect(amhsConnectionService).not.toEqual(null);
    }));

    describe('list', () => {
        it('should return an array of amhsConnections', inject((amhsConnectionService: AMHSConnectionService) => {

            let expectedResponse = Array<IAMHSConnection>();

            amhsConnectionService.list();

            httpBackend
                .expect('GET', `${restangular.configuration.baseUrl}/${endpoint}?size=20`)
                .respond(200, expectedResponse);

            httpBackend.flush();
        }));
    });

    describe('create', () => {
        it('should create a single amhsConnection', inject((amhsConnectionService: AMHSConnectionService) => {

            amhsConnectionService.create(amhsConnection);

            httpBackend
                .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
                .respond(200, amhsConnection);

            httpBackend.flush();
        }));
    });

    describe('update', () => {
        it('should update a single amhsConnection', inject((amhsConnectionService: AMHSConnectionService) => {

            amhsConnection.id = 1;

            amhsConnectionService.update(amhsConnection, amhsConnection.id);

            httpBackend
                .expect('PUT', `${restangular.configuration.baseUrl}/${endpoint}/${amhsConnection.id}`)
                .respond(200, amhsConnection);

            httpBackend.flush();
        }));
    });

    describe('delete', () => {
        it('should delete a single amhsConnection', inject((amhsConnectionService: AMHSConnectionService) => {

            amhsConnection.id = 1;

            amhsConnectionService.delete(amhsConnection.id);

            httpBackend
                .expect('DELETE', `${restangular.configuration.baseUrl}/${endpoint}/${amhsConnection.id}`)
                .respond(200, null);

            httpBackend.flush();
        }));
    });

});
