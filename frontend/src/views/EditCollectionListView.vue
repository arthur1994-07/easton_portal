<template>
	<two-button-dialog ref="removeDialog" title="Delete Collection" successText="Delete"
		cancelText="Cancel" :full-width="$q.screen.lt.md"
	>
		<template v-slot="{data}">
			<q-card-section>
				<div class="text-primary">Collection {{ data.name }} will be deleted</div>
			</q-card-section>
		</template>
	</two-button-dialog>
	<two-button-dialog ref="displayDialog" title="Collection Display" successText="Exit" cancelText="Close" :full-width="$q.screen.lt.md">
		<template v-slot="{data}">
			<q-card-section>
				<image-collection-display-component :images="data" />
			</q-card-section>
		</template>
	</two-button-dialog>
	<two-button-dialog ref="createDialog" :title="createMode ? 'Create Collection' : 'Edit Collection'" :successText="createMode ? 'Create' : 'Edit'" 
		:full-width="$q.screen.lt.md"
	>
		<template v-slot="{data}">
			<q-card-section>
				<div class="items-center q-my-sm">
					<q-input v-model="data.name" bg-color="secondary" :dark="true" filled
						label-color="white" label="Name"
						:rules="[
							val => !!val || 'Field is required',
						]"
					/>
					<q-select 
						v-model="data.year"
						filled 
						:dark="true"
						bg-color="secondary"
						text-color="primary"
						label-color="primary"
						:options="listOfYears"
					>
						<template v-slot:hint>
							Field hint
						</template>

						<template v-slot:append>
							<q-icon name="event" />
						</template>
					</q-select>
					<q-toggle 
						v-model="data.isProtected" 
						class="text-primary"
						:label="data.isProtected ? 'Protected' : 'Unprotected'"
						color="dark" 
						keep-color
						icon="mdi-checkbox-blank-circle" 
					/>
				</div>
				<div>
					<q-btn class="row items-start" color="info" @click="importCollectionPictures(false, null)">
						<q-icon name="mdi-import" color="white" />
						<q-tooltip>import file</q-tooltip>
					</q-btn>
					<q-item class="text-primary items-center">
						uploaded file : 
						<q-icon v-if="base64Profiles != null" name="mdi-file" color="green" size="lg" />
					</q-item>
				</div>
			</q-card-section>
		</template>
	</two-button-dialog>
	<two-button-dialog ref="requestDialog" title="Quotation request" successText="Request" :full-width="$q.screen.lt.md" cardStyle="max-width: 50vw; width : 50vw">
		<template v-slot="{data}">
			<q-card-section>
				<div class="items-center q-my-sm">
					<q-input v-model="data.remarks" bg-color="grey-3" :dark="false" filled
						label-color="primary" label="remarks"
						:rules="[
							val => !!val || 'Field is required',
						]"
					/>
				</div>
			</q-card-section>
		</template>
	</two-button-dialog>
	<q-page class="q-pa-sm">
		<q-card class="bg-secondary">
			<q-card-section class="row justify-between">
				<div class="text-section-color text-h6">Collection List</div>
				<div>
					<q-btn v-if="haveRights" push class="q-mx-xs" color="white" @click="editAction(null)">
						<q-icon color="primary" name="mdi-plus" />
						<q-tooltip>Collection</q-tooltip>
					</q-btn>
				</div>
			</q-card-section>
			<q-separator dark />
			<q-page class="collection-grid-section q-ma-xl">
				<div v-if="selectedCollection == null" class="text-black container">
					<q-item-section class="collection-grid">
						<div v-for="(collection, index) in collectionList" :key="index" class="collection-card">
							<div class="collection-image">
								<img :src="collection.image" :alt="collection.name">
								<div class="collection-actions">
									<q-btn v-if="haveRights" class="action-btn" @click="editAction(collection)">
										<q-icon name="mdi-pencil" />
									</q-btn>
									<q-btn v-if="haveRights" class="action-btn" @click="removeAction(collection)">
										<q-icon name="mdi-delete" />
									</q-btn>
									<q-btn v-if="haveRights" class="action-btn" @click="importCollectionPictures(!createMode, collection)">
										<q-icon name="mdi-image" />
									</q-btn>
									<q-btn class="action-btn" @click="requestAction(collection)">
										<q-icon name="mdi-file-document" />
									</q-btn>
									<q-btn class="action-btn" @click="displayAction(collection)">
										<q-icon name="mdi-eye" />
									</q-btn>
								</div>
							</div>
							<div class="collection-info">
								<h3 class="collection-name"> {{ collection.name }}</h3>
							</div>
						</div>
					</q-item-section>

					<div v-if="totalPages > 1" class="pagination">
						<q-btn
							class="pagination-btn prev-btn"
							:disabled="currentPage === 1"
							aria-label="Previous page"
							@click="goToPage(currentPage -1)"
						>
							<q-icon name="mdi-menu-left" />
						</q-btn>

						<div class="pagination-numbers">
							<q-btn
								v-for="page in visiblePages" 
								:key="page"
								class="pagination-btn page-btn"
								:class="{ active: page === currentPage }"
								@click="goToPage(page)"
							>
								{{ page }}
							</q-btn>
						</div>

						<q-btn
							class="pagination-btn next-btn" 
							:disabled="currentPage === totalPages"
							aria-label="Next page"
							@click="goToPage(currentPage + 1)"
						>
							<q-icon name="mdi-menu-right" />
						</q-btn>
					</div>

					<div class="pagination-info">
						<p>Showing {{ startItem }} - {{ endItem }} of {{ totalProducts }} products</p>
					</div>
				</div>
			</q-page>
		</q-card>
	</q-page>
</template>

<script>
import { ref, defineComponent, onMounted, readonly, watch, computed } from "vue";
import { useStore } from 'vuex';
import { dataUrlToBase64String, fileToDataUrl, showFileDialog } from '../script/utils/utils.js'
import * as PopupDialog from '../script/utils/PopupDialog.js'
import TwoButtonDialog from "../components/TwoButtonDialog.vue";
import CurrentUserService from "../script/services/CurrentUserService.js";
import CollectionService from "../script/services/CollectionService.js";
import WebService from "../script/services/WebService.js";
import RequestService from "../script/services/RequestService.js";
import ImageCollectionDisplayComponent from "../components/ImageCollectionDisplayComponent.vue";



export default defineComponent ({
	components: {
		TwoButtonDialog,
		ImageCollectionDisplayComponent,
	},
	setup() {
		const store = useStore()
		
		const createMode = ref(false)
		const requestDialog = ref(null)
		const createDialog = ref(null)
		const displayDialog = ref(null)
		const path = ref(null)
		const currentUser = ref(null)
		const model = ref(null)
		const accessToken = ref(null)

		const currentPage = ref(1);
		const itemsPerPage = ref(5);
		const selectedCollection = ref(null);
		const totalProducts = ref(0)

		const rights = ref([])
		const imageCollections = ref([])
		const collections = ref([])
		const base64Profiles = ref([])
		const listOfYears = ref([])
		const items = ref([])

		const collectionList = ref([{
			id: null,
			name: null,
			image: null,
		}])

		const haveRights = computed(() => rights?.value.find(s => s == 'edit_collection') != null)
		const totalPages = computed(() => Math.ceil(totalProducts.value / itemsPerPage.value));
		const startItem = computed(() => (currentPage.value - 1) * itemsPerPage.value + 1);
		const endItem = computed(() => Math.min(currentPage.value * itemsPerPage.value, totalProducts.value));
	
		const visiblePages = computed(() => {
			const pages = [];
			const maxVisible = 5;
			let start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2));
			let end = Math.min(totalPages.value, start + maxVisible - 1);
			
			if (end - start + 1 < maxVisible) {
				start = Math.max(1, end - maxVisible + 1);
			}
			
			for (let i = start; i <= end; i++) {
				pages.push(i);
			}
			
			return pages;
		});

		const columns = readonly([
			{ name: 'name', required: true, label: 'name', align: 'left', field: 'name', sortable: true },
			{ name: 'actions', label: 'actions', align: 'center', field: 'actions' }
		])

		const displayAction = async (item) => {	
			resetButtonHover()
			if (item == null) return
			imageCollections.value = await CollectionService.findImageById({ id: item.id }, path.value.url)
			let data = await displayDialog.value.run({
				images : imageCollections.value
			})
			if (data == null) return
		}

		const removeDialog = ref(null)
		const removeAction = async (item) => {
			resetButtonHover()

			let data = await removeDialog.value.run({ name: item.name });
			if (data == null) return

			try {
				await CollectionService.remove({ id: item.id }, accessToken.value, path.value.url);
				loadPageData(currentPage.value)
				items.value = await CollectionService.list(accessToken.value, path.value.url)
				PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully removed collection");
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		const editAction = async (item) => {
			resetButtonHover()
			getListOfYears()
			createMode.value = item == null
			base64Profiles.value = null
			let data = await createDialog.value.run({
				id: item?.id,
				name: item?.name,
				year: item?.year,
				isProtected: item == null ? false : item?.isProtected,
			}, {
				successCheck : (data) => data.name != "" && data.year != null 
			});
			if (data == null) return;
			try {
				if (createMode.value) {
					await CollectionService.create(
						{
							name: data.name,
							year: data.year,
							isProtected: data.isProtected,
							image: base64Profiles?.value
						}
						, accessToken.value, path.value.url)
					loadPageData(currentPage.value)
					items.value = await CollectionService.list(accessToken.value, path.value.url)
					PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully created collection")
				} else {
					await CollectionService.update(
						{
							id: data.id,
							name: data.name,
							year: data.year,
							isProtected: data.isProtected,
							image: base64Profiles?.value
						}
						, accessToken.value, path.value.url)
					loadPageData(currentPage.value)
					items.value = await CollectionService.list(accessToken.value, path.value.url)
					PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully edited collection")
				}
				items.value = await CollectionService.list(accessToken.value, path.value.url)
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		const importCollectionPictures = async (isMultiple, item) => {
			base64Profiles.value = null
			collections.value = null
			collections.value = await showFileDialog(".png", isMultiple)
			if (collections.value == null || collections.value.length <= 0) return

			var urls = isMultiple ? [] : null 

			urls = isMultiple ? await Promise.all(collections.value.map(async s => await fileToDataUrl(s))) :
				await fileToDataUrl(collections.value)
			if (urls == null || urls.length <= 0) return

			base64Profiles.value = isMultiple ? urls.map(s => dataUrlToBase64String(s)) : dataUrlToBase64String(urls);
			if (isMultiple && item != null) batchUpdateImages(item)
		}

		const batchUpdateImages = async (item) => {
			try {
				if (base64Profiles.value != null) {	
					let requestList = []
					requestList = (base64Profiles.value.map(s => ({
						id: item.id,
						image: s,
						format: "pgm"
					})))
					await Promise.all(requestList.map(s => 
						CollectionService.updateImages(s,accessToken.value, path.value.url)))
				}
				loadPageData(currentPage.value)
				items.value = await CollectionService.list(accessToken.value, path.value.url)
				PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully added collection photos")
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}
		
		const requestAction = async (item) => {
			resetButtonHover()
			if (item == null) return
			let data = await requestDialog.value.run({
				remarks: item?.remarks
			}, {
				successCheck : (data) => data.remarks != ""
			});
			if (data == null) return;
			try {
				// request service create quotation request 
				await RequestService.create({
					customerId: currentUser.value.uuid,
					customerName: currentUser.value.username,
					customerEmail: currentUser.value.email,
					collectionName: item.name,
					remarks: data.remarks
				})
				PopupDialog.show(store, PopupDialog.SUCCESS, "Successfully requested new quotation")

			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		const getListOfYears = () => {
			let currentYear = new Date().getFullYear()
			const earliestYear = 2010;

			while (currentYear >= earliestYear) {
				listOfYears.value.push(currentYear)
				currentYear -= 1;
			}
		}

		const goToPage = (page) => {
			if (page >= 1 && page <= totalPages.value) {
				currentPage.value = page;
				// Scroll to top of product grid
				document.querySelector('.collection-grid-section').scrollIntoView({ 
					behavior: 'smooth',
					block: 'start'
				});
			}
		};
		
		const resetButtonHover = () => {
			if (document.activeElement) document.activeElement.blur(); // reset hover UI hover state
		}

		const loadPageData = async (currentPage) => {
			try {

				const collectionsData = await CollectionService.getImagesPage(path.value.url, {
					pageIndex: currentPage -1, 
					itemsPerPage: itemsPerPage.value
				}, accessToken.value)
				totalProducts.value = collectionsData.totalItems
				
				collectionList.value = await collectionsData.items.map(s => ({
					id: s.id,
					name: s.name,
					year: s.year,
					isProtected: s.isProtected,
					image: ref(new URL("data:image/png;base64, "+ s.image, import.meta.url).href)
				}))
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		}

		watch(currentPage, async () => loadPageData(currentPage.value))



		onMounted(async () => {
			try{
				currentUser.value = await CurrentUserService.current()
				rights.value = await CurrentUserService.permission()
				path.value = await WebService.getInfo()

				accessToken.value = await CollectionService.webAuthenticate(currentUser.value.uuid, currentUser.value.username, path.value.url)
				loadPageData(currentPage.value)
				items.value = await CollectionService.list(accessToken.value, path.value.url)
			} catch(err) {
				PopupDialog.show(store, PopupDialog.FAILURE, err.message)
			}
		})

		return { model, items, accessToken, imageCollections, path, columns, collections, base64Profiles,
			selectedCollection, currentPage, itemsPerPage, totalPages, collectionList, totalProducts,
			listOfYears, createMode, displayDialog, createDialog, removeDialog, requestDialog, 
			visiblePages, startItem, endItem, haveRights, batchUpdateImages, importCollectionPictures, 
			removeAction, editAction, displayAction, requestAction, goToPage }
	}
})
</script>

<style>
	.collection-grid-section {
		padding: 1rem 0; 
	}

	.section-header {
		text-align: center;
		margin-bottom: 4rem;
	}

	.section-title {
		font-family: Avenir, Helvetica, Arial, sans-serif;
		font-size: 2.5rem;
		font-weight: 600;
		color: #1C4854;
		margin-bottom: 1rem;
	}

	.section-subtitle {
		font-size: 1.1rem;
		color: #1C4854;
		opacity: 0.8;
	}

	.collection-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 2rem;
		margin-bottom: 3rem;
	}

	.collection-card {
		position: relative;
		overflow: hidden;
		transition: transform 0.3s ease;
		will-change: transform;
		backface-visibility: hidden;
	}

	.collection-card:hover {
		transform: translateY(-5px);
	}

	.collection-image {
		position: relative;
		overflow: hidden;
		border-radius: 8px;
		aspect-ratio: 1;
		min-width: auto;
	}

	.collection-image img {
		width: 100%;
		height: 100%;
		object-fit: cover;
		transition: transform 0.5s ease;
	}

	.collection-card:hover .collection-image img {
		transform: scale(1);
	}

	.collection-actions {
		position: absolute;
		bottom: -50px;
		left: 0;
		right: 0;
		display: flex;
		justify-content: center;
		gap: 0.5rem;
		padding: 0.5rem;
		background-color: rgba(12, 66, 36, 0.4);
		transition: bottom 0.3s ease;
	}

	.collection-card:hover .collection-actions {
		bottom: 0;
	}

	.action-btn {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 20px;
		height: 20px;
		border-radius: 50%;
		background-color: white;
		border: 1px solid #eee;
		color: var(--dark-text);
		transition: all 0.3s ease;
	}

	.action-btn:hover {
		background-color: var(--primary-color);
		color: white;
	}

	.collection-info {
		padding: 1rem 0.5rem;
		text-align: center;
	}

	.collection-name {
		font-size: 1.3rem;
		font-weight: 500;
		margin-bottom: 0.5rem;
		font-family: Avenir, Helvetica, Arial, sans-serif;
		line-height: 1.2;
		color: rgb(30, 62, 38);
	}

	.collection-price {
		font-weight: 600;
		color: var(--primary-color);
	}

	/* Pagination Styles */
	.pagination {
		display: flex;
		justify-content: center;
		align-items: center;
		gap: 0.5rem;
		margin: 2rem 0;
	}

	.pagination-btn {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 40px;
		height: 40px;
		border: 1px solid #ddd;
		background-color: white;
		color: var(--dark-text);
		border-radius: 6px;
		transition: all 0.3s ease;
		font-weight: 500;
	}

	.pagination-btn:hover:not(:disabled) {
		background-color: var(--primary-color);
		color: white;
		border-color: var(--primary-color);
	}

	.pagination-btn:disabled {
		opacity: 0.5;
		cursor: not-allowed;
	}

	.pagination-btn.active {
		background-color: #1a4d27;
		color: white;
		border-color: var(--primary-color);
	}

	.pagination-numbers {
		display: flex;
		gap: 0.25rem;
	}

	.pagination-info {
		text-align: center;
		margin-top: 1rem;
		color: #0b2311;
	}

	.pagination-info p {
		color: var(--dark-text);
		opacity: 0.7;
		font-size: 0.9rem;
	}

	@media (max-width: 1200px) {
		.collection-grid {
			grid-template-columns: repeat(4, 1fr);
		}
	}

	@media (max-width: 1024px) {
		.collection-grid {
			grid-template-columns: repeat(3, 1fr);
		}
	}

	@media (max-width: 768px) {
		.collection-grid {
			grid-template-columns: repeat(2, 1fr);
			gap: 1.5rem;
		}
		
		.section-title {
			font-size: 2rem;
		}
		
		.pagination {
			gap: 0.25rem;
		}
		
		.pagination-btn {
			width: 36px;
			height: 36px;
		}
	}

	@media (max-width: 480px) {
		.collection-grid {
			grid-template-columns: 1fr;
		}
		
		.section-header {
			margin-bottom: 2rem;
		}
		
		.section-title {
			font-size: 1.8rem;
		}
	}
</style>