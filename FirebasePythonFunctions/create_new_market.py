import yfinance as yf
import firebase_admin
import datetime
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate('./bearvbull_service_account_key.json')

app = firebase_admin.initialize_app(cred)

db = firestore.client()

def createNewMarket(ticker):
	doc_ref = db.collection(u'live_prediction_market_info').document(u'')