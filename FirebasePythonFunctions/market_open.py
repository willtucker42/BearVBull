import firebase_admin
import yfinance as yf
from firebase_admin import credentials, firestore

cred = credentials.Certificate('./bearvbull_service_account_key.json')
default_app = firebase_admin.initialize_app(cred)
db = firestore.client()


def getTodaysOpen(ticker):
    ticker_info = yf.Ticker(ticker).info
    # print(ticker_info.keys())

    todaysMarketOpen = ticker_info['regularMarketOpen']
    previousMarketClose = ticker_info['regularMarketPreviousClose']

    if todaysMarketOpen >= previousMarketClose:
        updateBets('green')
    elif todaysMarketOpen < previousMarketClose:
        updateBets('red')
    else:
        print('error1')

    print('todays market open: ' + str(todaysMarketOpen))
    print('previous market open: ' + str(previousMarketClose))


def greenOpen():
    print("Starting green open update process")



def redOpen():
    print("Starting red open update process")


def updateBets(open_color):
    if open_color == 'green':
        greenOpen()
    else:
        redOpen()


getTodaysOpen('SPY')
