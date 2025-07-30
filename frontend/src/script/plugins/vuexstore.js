'use strict'

import { createStore } from 'vuex'
import store_list from '../utils/store_list'

const store = createStore({ modules : store_list })
export default store