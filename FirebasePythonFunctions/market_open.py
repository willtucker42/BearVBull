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
        update('green')
    elif todaysMarketOpen < previousMarketClose:
        update('red')
    else:
        print('error1')

    print('todays market open: ' + str(todaysMarketOpen))
    print('previous market open: ' + str(previousMarketClose))


def update(open_color):
    print("Starting green open update process")
    user_winnings_dict = {}
    docs = db.collection(u'all_user_bets').where(u'bet_status', u'==', "active").stream()
    winnings = 0
    for doc in docs:
        bet_dict = doc.to_dict()
        if bet_dict['bet_side'] == 'BULL' and open_color == 'green':
            winnings = bet_dict['win_multiplier'] * bet_dict['bet_amount']
            # print("User ", bet_dict['user_id'], " won ", winnings, "(", bet_dict['win_multiplier'], " * ",
            #       bet_dict['bet_amount'], ")")
        elif bet_dict['bet_side'] == 'BEAR' and open_color == 'red':
            winnings = bet_dict['win_multiplier'] * bet_dict['bet_amount']
            # print("User ", bet_dict['user_id'], " won ", winnings, "(", bet_dict['win_multiplier'], " * ",
            #       bet_dict['bet_amount'], ")")
        else:
            pass
            # print("User", bet_dict['user_id'], " lost ", bet_dict['bet_amount'])
        user_winnings_dict[bet_dict['user_id']] = winnings

    updateUserAccounts(user_winnings_dict=user_winnings_dict)


def updateUserAccounts(user_winnings_dict):
    # user_id in user_bet document == username in users document
    i = 0
    batch = db.batch()

    for thing in user_winnings_dict:
        print(thing)

    for user in user_winnings_dict.keys():
        print("Updating user: ", user, ". They won ", user_winnings_dict[user])
        user_doc = db.collection(u'users').where(u'username', u'==', user).limit(1).get()
        for doc in user_doc:
            user_dict = doc.to_dict()
            og_balance_avail = user_dict['balance_available']
            print("Adding ", str(user_winnings_dict[user]), " to og balance of ", str(og_balance_avail))
            # user_doc.reference.update()
            batch.update(doc.reference, {u'balance_available': og_balance_avail + user_winnings_dict[user]})
        i += 1
        if i == 499:
            # right here we commit the batch and create a new instance of a batch
            print("committing and resetting batch...")
            batch.commit()
            batch = db.batch()
            i = 0
    batch.commit()
    # user_doc.update()


getTodaysOpen('SPY')
