export const SUCCESS = 'success'
export const FAILURE = 'danger'
export const WARNING = 'warning'


export const show = (store, type, message) => {
	store.dispatch('ui/notify', {
		type: type,
		text: message
	})
}