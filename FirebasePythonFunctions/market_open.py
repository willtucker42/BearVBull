import yfinance as yf
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

# Use a service account.
cred = credentials.Certificate('./bearvbull_service_account_key.json')

app = firebase_admin.initialize_app(cred)

db = firestore.client()


def getTodaysOpen(ticker):
    ticker_info = yf.Ticker(ticker).info

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
    docs = db.collection(u'all_user_bets').where(u'bet_status', u'==', "active").stream()
    for doc in docs:
        bet_dict = doc.to_dict()
        winnings = 0
        if bet_dict['bet_side'] == 'BULL':
            winnings = bet_dict['win_multiplier'] * bet_dict['bet_amount']
            print("User ", bet_dict['user_id'], " won ", winnings, "(", bet_dict['win_multiplier'], " * ",
                  bet_dict['bet_amount'], ")")
        else:
            print("User", bet_dict['user_id'], " lost ", bet_dict['bet_amount'])
        updateUserAccount(winnings=winnings, user_id=bet_dict['user_id'])


def updateUserAccount(winnings, user_id):
    print("Updating user: ", user_id, ". They won ", winnings)


def redOpen():
    print("Starting red open update process")


def updateBets(open_color):
    if open_color == 'green':
        greenOpen()
    else:
        redOpen()


getTodaysOpen('SPY')
