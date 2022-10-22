// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
// The Cloud Functions for Firebase SDK to create Cloud Functions and set up triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

exports.addMessage = functions.https.onRequest(async (req, res) => {
  // Grab the text parameter.
  const original = req.query.text;
  // Push the new message into Firestore using the Firebase Admin SDK.
  const writeResult = await admin
    .firestore()
    .collection('messages')
    .add({ original: original });
  // Send back a message that we've successfully written the message
  res.json({ result: `Message with ID: ${writeResult.id} added.` });
});

// Listens for new messages added to /messages/:documentId/original and creates an
// uppercase version of the message to /messages/:documentId/uppercase
exports.makeUppercase = functions.firestore
  .document('/messages/{documentId}')
  .onCreate((snap, context) => {
    // Grab the current value of what was written to Firestore.
    const original = snap.data().original;

    // Access the parameter `{documentId}` with `context.params`
    functions.logger.log('Uppercasing', context.params.documentId, original);

    const uppercase = original.toUpperCase();

    // You must return a Promise when performing asynchronous tasks inside a Functions such as
    // writing to Firestore.
    // Setting an 'uppercase' field in Firestore document returns a Promise.
    return snap.ref.set({ uppercase }, { merge: true });
  });

exports.updateMarketInfoOnNewBet = functions.firestore
  .document('/all_user_bets/{id}')
  .onCreate(async (snap, context) => {
    const docData = snap.data();
    functions.logger.log('updating ', context.params.id, docData);
//     console.log(docData);
    const livePredictionMarketInfoRef = admin
      .firestore()
      .collection('live_prediction_market_info')
      .doc(docData['market_id']);
      
    functions.logger.log('the marketDoc reference', livePredictionMarketInfoRef);
    const marketDoc = await livePredictionMarketInfoRef.get();
    functions.logger.log('the marketDoc ', marketDoc);
    const marketData = marketDoc.data();
    const currentBiggestBullBet = Number(marketData["biggest_bull_bet"]);
	const currentBiggestBearBet = Number(marketData["biggest_bear_bet"]);
    const betAmount = Number(docData["bet_amount"]);
    functions.logger.log('is this not a valid number? ', betAmount);
    if (docData["bet_side"] === "BEAR") {
      livePredictionMarketInfoRef.update({
      		bear_headcount : admin.firestore.FieldValue.increment(1),
      		bear_total : admin.firestore.FieldValue.increment(betAmount),
      		biggest_bear_bet : (docData['bet_amount'] > currentBiggestBearBet ? docData['bet_amount'] : currentBiggestBearBet)
    	});
    } else {
      livePredictionMarketInfoRef.update({
      		bull_headcount : admin.firestore.FieldValue.increment(1),
      		bull_total : admin.firestore.FieldValue.increment(betAmount),
      		biggest_bull_bet : (docData['bet_amount'] > currentBiggestBullBet ? docData['bet_amount'] : currentBiggestBullBet)
    	});
    }
    
  });
