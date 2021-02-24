let flightMovementWithNoId = {
  'associated_account': 'Air Botswana',
  'associated_account_id': 1,
  'actual_departure_time': null,
  'actual_mtow': null,
  'aircraft_type': 'Sputnik',
  'arrival_ad': 'aerodrome2',
  'arrival_charges': null,
  'arrival_time': '1234',
  'associated_aircraft': null,
  'average_mass_factor': null,
  'crew_members': null,
  'date_of_flight': '2016-07-13T00:00:00Z',
  'dep_ad': 'FFFF',
  'dep_time': '0415',
  'departure_charges': null,
  'dest_ad': 'FFFF',
  'enroute_charges': null,
  'entry_time': null,
  'exit_time': null,
  'flight_id': 'FFF209',
  'flight_notes': null,
  'flight_type': 'N',
  'flight_rules': 'I',
  'flight_status': 'PENDING',
  'fpl_crossing_cost': null,
  'fpl_crossing_distance': null,
  'fpl_route': 'DCT',
  'initial_fpl_data': '(FPL-BOT209-IS\r\n-01AT45/M-SDFGR/C\r\n-FBSK0415\r\n-N0250F190 DCT GSV UG653 NESEK DCT JSV DCT\r\n-FAOR0050 FBSK\r\n-PBN/B4 NAV/GPS DOF/160713 REG/A2ABN EET/FAJA0010 OPR/AIR BOTSWANA +2673688573 RMK/SARNML)\r\n',
  'item18_dep': 'a',
  'item18_dest': 'b',
  'item18_reg_num': 'A2ABN',
  'item18_status': 'PENDING',
  'movement_type': 'PENDING',
  'nominal_crossing_cost': null,
  'nominal_crossing_distance': null,
  'other_info': 'PBN/B4 NAV/GPS DOF/160713 REG/A2ABN EET/FAJA0010 OPR/AIR BOTSWANA +2673688573 RMK/SARNML',
  'parking_charges': null,
  'parking_hours': 123,
  'passengers_chargeable_domestic': 1,
  'passengers_chargeable_intern': 1,
  'passengers_joining_adult': null,
  'passengers_transit_adult': null,
  'passengers_child': 5,
  'prepaid_amount': null,
  'radar_crossing_cost': null,
  'radar_crossing_distance': null,
  'other_info_rmk': 'Test Remark',
  'spatia_fpl_object_id': 191983,
  'status': 'PENDING',
  'total_charges': null,
  'unspecified_dep_aerodrome': 'aerodrome1',
  'unspecified_arrival_aerodrome': 'aerodrome2',
  'user_specified_crossing': 'test',
  'wake_turb': 'M',
  'item18_rmk': 'Test Remark',
  'user_crossing_distance': 5,
  'parking_time': 1234,
  'tasp_charge': 1,
  'elapsed_time': '1234',
  'cruising_speed_or_mach_number': 'N123'
}

let flightMovementWithId = {
  'id': 2
};

let aircraftType = {
  'aircraft_image': null,
  'aircraft_name': 'Sputnik',
  'aircraft_type': 'Sputnik',
  'id': 1,
  'manufacturer': 'USSR',
  'maximum_takeoff_weight': 2.4,
  'wake_turbulence_category': {
    id: 1,
    name: 'L'
  }
}

let account = {
  'id': 1,
  'name': 'Air Botswana'
}

Object.assign(flightMovementWithId, flightMovementWithNoId);


let incompleteFlightMovement = Object.assign({}, flightMovementWithId); // shallow copy by value
incompleteFlightMovement.status = 'INCOMPLETE';
incompleteFlightMovement.id = 3;

module.exports = [{
    request: {
      path: '/aircraft-types',
      method: 'GET',
    },
    response: {
      data: {
        content: [aircraftType]
      }
    }
  },
  {
    request: {
      path: '/accounts',
      method: 'GET',
    },
    response: {
      data: {
        content: [account]
      }
    }
  },
  {
    request: {
      path: '/flightmovements/filters?status=INCOMPLETE&',
      method: 'GET',
      data: {
        status: 'INCOMPLETE&',
      }
    },
    response: {
      data: ''
    }
  },
  {
    request: {
      path: '/flightmovements/filters?status=PENDING&',
      method: 'GET',
      data: {
        status: 'PENDING&',
      }
    },
    response: {
      data: {
        content: [flightMovementWithId]
      }
    }
  },
  {
    request: {
      path: '/flightmovements/filters?type=REG_OVERFLIGHT&',
      method: 'GET',
      data: {
        status: 'REG_OVERFLIGHT&',
      }
    },
    response: {
      data: ''
    }
  },
  {
    request: {
      path: '/flightmovements',
      method: 'GET'
    },
    response: {
      data: {
        content: [flightMovementWithId, incompleteFlightMovement]
      }
    }
  },
  {
    request: {
      path: '/flightmovements',
      method: 'POST',
      data: flightMovementWithNoId
    },
    response: {
      data: {
        content: [flightMovementWithId, incompleteFlightMovement]
      }
    }
  },
  {
    request: {
      path: '/flightmovements',
      method: 'PUT',
      data: flightMovementWithId
    },
    response: {
      data: {
        content: [flightMovementWithId, incompleteFlightMovement]
      }
    }
  },
  {
    request: {
      path: '/flightmovements/2',
      method: 'DELETE'
    },
    response: ''
  }
];
