import yfinance as yf
import firebase_admin
import datetime
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate('./bearvbull_service_account_key.json')

app = firebase_admin.initialize_app(cred)

db = firestore.client()


def createDocumentNameForMarket(tkr):
    next_trading_day = str(datetime.date.today() + datetime.timedelta(days=1)).replace('-', '_')

    print(tkr + "_" + next_trading_day)
    return tkr + "_" + next_trading_day


def createNewMarket(ticker):
    doc_ref = db.collection(u'live_prediction_market_info').document(u'')


createDocumentNameForMarket("SPY")
