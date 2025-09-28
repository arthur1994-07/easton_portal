<template>
	<q-layout>
		<q-page-container>
			<q-page>
				<div class="row items-center justify-center" 
					style="min-height: inherit;"
				>
					<q-card class="bg-primary" style="min-width: 55vw">
						<q-card-section>
							<div class="q-ma-md">
								<q-img :src="companyLogo" class="icon q-ma-lg justify-center items-center" fit="contain" 
									style="max-height: 15vh;"
								/>
								<div class="col-1 q-ma-sm text-h6 text-white">Login portal</div>
							</div>
						</q-card-section>
						<q-separator dark />
						<q-card-section class="column">
							<div v-for="d in domains" :key="d.id" class="ma-2">
								<q-btn push class="full-width" color="secondary" type="button" @click="login(d)">
									Login
								</q-btn>
							</div>
						</q-card-section>
					</q-card>
				</div>
			</q-page>
		</q-page-container>
	</q-layout>
</template>


<script>
'use strict'

import { defineComponent, ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import * as PopupDialog from '../script/utils/PopupDialog.js'
import authService from "../script/services/AuthService.js"
import oAuthService from "../script/services/OAuthService.js"

export default defineComponent({
	setup() {
		const store = useStore()
		const router = useRouter()
		const networkError = ref(false)
		const domains = ref(null)
		const oauthSignIn = ref(false)


		const companyLogo = ref(new URL('../assets/logo.png', import.meta.url).href)
		const noOAuthSet = computed(() => domains.value?.length == 0)
		const autoLogin = computed(() => store.state.ui.autoLogin)


		const checkAuthentication = ({authenticated, idToken}) => {
			store.dispatch("ui/setAutoLogin", false)
			if (!authenticated) return;
			router.push("/")
			store.dispatch("ui/setIdToken", idToken)
		}


		const login = async (domain) => {
			try {
				const state = authService.generateOAuthState();
				window.sessionStorage.setItem("oauth-id", domain.id)
				window.sessionStorage.setItem("state", state)

				const url = await authService.oAuth.redirect(domain.id, "/login", state, false)
				window.location.replace(url)
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		const domainRequest = async () => {
			try {
				const data = await oAuthService.get()
				domains.value = data.filter(s => !s.disable)
				networkError.value = false
			} catch(err) {
				networkError.value |= err.message == "Network Error"
				setTimeout(() => domainRequest(), 2000)
			}
		}

		onMounted(async () => {
			await domainRequest()
			// if (noOAuthSet.value) return
		})

		const signInAction = async (id, code) => {
			try {
				oauthSignIn.value = true
				const response = await authService.oAuth.signIn(id, code, "/login");
				checkAuthentication(response)
			} catch(err) {
				oauthSignIn.value = false
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		onMounted(() => {
			oauthSignIn.value = false
			let params = new URLSearchParams(window.location.search)
			if (!params.has("code") || !params.has("state")) return
			
			const id = window.sessionStorage.getItem("oauth-id")
			// const storedState = window.sessionStorage.getItem("state")
			window.sessionStorage.removeItem("oauth-id")
			window.sessionStorage.removeItem("state")

			// if (!id) throw Error("Missing OAuth id information")
			if (!id) return
			// if (storedState !== params.get("state")) throw Error("Invalid state")
			signInAction(id, params.get("code"))
		})


		return { domains, noOAuthSet, autoLogin, companyLogo, login }
	}
})
</script>

<style>

.icon {
	min-height: auto;
	min-width: auto;
}
</style>
