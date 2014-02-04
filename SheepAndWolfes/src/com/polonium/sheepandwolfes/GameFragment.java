package com.polonium.sheepandwolfes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.polonium.sheepandwolfes.entity.field.GameField;
import com.polonium.sheepandwolfes.entity.game.GameState;
import com.polonium.sheepandwolfes.entity.player.OnMakeMoveListener;
import com.polonium.sheepandwolfes.entity.player.Player;
import com.polonium.sheepandwolfes.entity.player.SheepPlayer;
import com.polonium.sheepandwolfes.entity.player.UniversalAIMinimax;
import com.polonium.sheepandwolfes.entity.player.UniversalAIMinimaxAlphaBeta;
import com.polonium.sheepandwolfes.entity.player.WolfsPlayer;
import com.polonium.sheepandwolfes.views.GameFieldView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that contain this fragment must implement the
 * {@link GameFragment.OnFragmentInteractionListener} interface to handle interaction events. Use the
 * {@link GameFragment#newInstance} factory method to create an instance of this fragment.
 * 
 */
public class GameFragment extends Fragment implements OnMakeMoveListener {

    GameFieldView gameFieldView;
    private GameField gameField = new GameField();
    private Player sheep;
    private Player wolfs;

    private OnFragmentInteractionListener mListener;
    private GameState mCurrentState;
    private int mCurrentSheep, mCurrentWolfs;
    private boolean needRestart = true;

    public static GameFragment newInstance() {
        GameFragment fragment = new GameFragment();
        return fragment;
    }

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        gameFieldView = (GameFieldView) view.findViewById(R.id.gameFieldView1);

        startGame(0, 1);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_replay:
            sheep.gameOver(true);
            wolfs.gameOver(true);
            mListener.closeDrawer();
            startGame(mCurrentSheep, mCurrentWolfs);
            return true;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startGame(int sheepLevel, int wolfsLevel) {
        mCurrentState = new GameState(0, 28, 29, 30, 31);
        changePlayers(sheepLevel, wolfsLevel);
        initGame(mCurrentState);
        needRestart = false;
    }

    private void initGame(GameState gs) {
        Player currentPlayer = getNextPlayer(gs).makeMove(gs, this);
        gameFieldView.setFieldTouchListener(currentPlayer.getFieldTouchListener());
        gameFieldView.setState(gs);
    }

    public void changePlayers(int sheepLevel, int wolfsLevel) {
        if (sheep != null) sheep.gameOver(true);
        if (sheepLevel == 0) {
            sheep = new SheepPlayer();
        } else if (sheepLevel < 4) {
            sheep = new UniversalAIMinimax(sheepLevel);
        } else {
            sheep = new UniversalAIMinimaxAlphaBeta(sheepLevel - 3);
        }
        if (wolfs != null) wolfs.gameOver(true);
        if (wolfsLevel == 0) {
            wolfs = new WolfsPlayer();
        } else if (wolfsLevel < 4) {
            wolfs = new UniversalAIMinimax(wolfsLevel);
        } else {
            wolfs = new UniversalAIMinimaxAlphaBeta(wolfsLevel - 3);
        }
        if (mCurrentState != null || needRestart) {
            initGame(new GameState(0, 28, 29, 30, 31));
            needRestart = false;
        }
        mCurrentSheep = sheepLevel;
        mCurrentWolfs = wolfsLevel;
    }

    public Player getNextPlayer(GameState state) {
        if (state.lastMove == GameState.WOLFS) return sheep;
        return wolfs;
    }

    @Override
    public void onMoveComlete(Player player, GameState state) {
        gameFieldView.setState(state);
        mCurrentState = state;
        if (!state.wolfsWin(gameField)) {
            // Toast.makeText(getActivity(), "Wolfs Wins", Toast.LENGTH_SHORT).show();
            showMessage("Волки победили");
            wolfs.gameOver(true);
            sheep.gameOver(true);
            needRestart = true;
            return;
        }
        if (state.sheepWin()) {
            // Toast.makeText(getActivity(), "Sheep Wins", Toast.LENGTH_SHORT).show();
            showMessage("Овечка спаслась");
            sheep.gameOver(true);
            wolfs.gameOver(true);
            needRestart = true;
            return;
        }
        Player current = getNextPlayer(state).makeMove(state, this);
        gameFieldView.setFieldTouchListener(current.getFieldTouchListener());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an interaction in this
     * fragment to be communicated to the activity and potentially other fragments contained in that activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html" >Communicating with Other
     * Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void openDrawer();

        public void closeDrawer();
    }

    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message).setPositiveButton("Еще раз?", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startGame(mCurrentSheep, mCurrentWolfs);
            }
        }).setNegativeButton("Участники", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.openDrawer();
            }
        });
        // Create the AlertDialog object and return it
        builder.create().show();
    }
}
