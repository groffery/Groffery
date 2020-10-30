export const login = (data) => {
    return{
        type: 'LOGIN',
        payload: data
    }
}

export const setState = (data) => {
    return{
        type: 'set',
        payload: data
    }
}
