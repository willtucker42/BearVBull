import yfinance as yf
import firebase_admin
import datetime
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate('./bearvbull_service_account_key.json')

app = firebase_admin.initialize_app(cred)

db = firestore.client()

new_market_tickers = ["META", "TSLA", "MSFT", "AAPL", "GOOG", "UBER", "PLTR", "NFLX", "AMZN", "SBUX", "DIS", "NVDA"]


def createDocumentNameForMarket(tkr):
    next_trading_day = str(datetime.date.today() + datetime.timedelta(days=1)).replace('-', '_')

    print(tkr + "_" + next_trading_day)
    return tkr + "-" + next_trading_day


def createNewMarket(ticker):
    doc_ref = db.collection(u'live_prediction_market_info').document(createDocumentNameForMarket(ticker))
    doc_ref.set({
        u'bear_headcount': 0,
        u'bear_total': 0,
        u'bet_status': u'live',
        u'biggest_bear_bet': 0,
        u'biggest_bull_bet': 0,
        u'bull_headcount': 0,
        u'bull_total': 0,
        u'end_time': datetime.datetime.now(),
        u'ticker': ticker
    })


for market in new_market_tickers:
    createNewMarket(market)
