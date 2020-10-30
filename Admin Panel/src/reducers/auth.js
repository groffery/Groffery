const initialState = {
  authenticated: false,
  user: null,
  loading: true,
};
const auth = (state = initialState, action) => {
  const { type, payload } = action;
  switch (type) {
    case "LOGIN":
      return {
        authenticated: true,
        user: payload,
        loading: false,
      };
    default:
      return state;
  }
};

export default auth;
