import yfinance as yf
from firebase_admin import credentials, firestore

cred = credentials.Certificate('./bearvbull_service_account_key.json')
default_app = firebase_admin.initialize_app(cred)
db = firestore.client()

def getTodaysOpen(ticker):
	ticker_info = yf.Ticker(ticker).info
	#print(ticker_info.keys())
		
	todaysMarketOpen = ticker_info['regularMarketOpen']
	previousMarketClose = ticker_info['regularMarketPreviousClose']
	
	if (todaysMarketOpen >= previousMarketClose):
		print('The market opened green today.')
		# updateBetsAndUsers('green')
	elif (todaysMarketOpen < previousMarketClose):
		print('The market opened red today.')
		# updateBetsAndUsers('red')
	else:
		print('error1')

	print('todays market open: ' + str(todaysMarketOpen))
	print('previous market open: ' + str(previousMarketClose))

getTodaysOpen('SPY')