import {combineReducers} from 'redux';
import changeState from './changeState';
import auth from './auth';

const reducers = combineReducers({
    changeState,
    auth
});

export default reducers;