package org.apache.maven.doxia.module.rtf;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

class SerifBoldItalic
    extends FontMetrics
{

    public static final CharMetrics[] metrics = {new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 389, 0, 67, -13, 370, 684 ), new CharMetrics( 555, 0, 136, 398, 536, 685 ),
        new CharMetrics( 500, 0, -33, 0, 533, 700 ), new CharMetrics( 500, 0, -20, -100, 497, 733 ),
        new CharMetrics( 833, 0, 39, -10, 793, 692 ), new CharMetrics( 778, 0, 5, -19, 699, 682 ),
        new CharMetrics( 333, 0, 98, 369, 302, 685 ), new CharMetrics( 333, 0, 28, -179, 344, 685 ),
        new CharMetrics( 333, 0, -44, -179, 271, 685 ), new CharMetrics( 500, 0, 65, 249, 456, 685 ),
        new CharMetrics( 570, 0, 33, 0, 537, 506 ), new CharMetrics( 250, 0, -60, -182, 144, 134 ),
        new CharMetrics( 333, 0, 2, 166, 271, 282 ), new CharMetrics( 250, 0, -9, -13, 139, 135 ),
        new CharMetrics( 278, 0, -64, -18, 342, 685 ), new CharMetrics( 500, 0, 17, -14, 477, 683 ),
        new CharMetrics( 500, 0, 5, 0, 419, 683 ), new CharMetrics( 500, 0, -27, 0, 446, 683 ),
        new CharMetrics( 500, 0, -15, -13, 450, 683 ), new CharMetrics( 500, 0, -15, 0, 503, 683 ),
        new CharMetrics( 500, 0, -11, -13, 487, 669 ), new CharMetrics( 500, 0, 23, -15, 509, 679 ),
        new CharMetrics( 500, 0, 52, 0, 525, 669 ), new CharMetrics( 500, 0, 3, -13, 476, 683 ),
        new CharMetrics( 500, 0, -12, -10, 475, 683 ), new CharMetrics( 333, 0, 23, -13, 264, 459 ),
        new CharMetrics( 333, 0, -25, -183, 264, 459 ), new CharMetrics( 570, 0, 31, -8, 539, 514 ),
        new CharMetrics( 570, 0, 33, 107, 537, 399 ), new CharMetrics( 570, 0, 31, -8, 539, 514 ),
        new CharMetrics( 500, 0, 79, -13, 470, 684 ), new CharMetrics( 832, 0, 63, -18, 770, 685 ),
        new CharMetrics( 667, 0, -67, 0, 593, 683 ), new CharMetrics( 667, 0, -24, 0, 624, 669 ),
        new CharMetrics( 667, 0, 32, -18, 677, 685 ), new CharMetrics( 722, 0, -46, 0, 685, 669 ),
        new CharMetrics( 667, 0, -27, 0, 653, 669 ), new CharMetrics( 667, 0, -13, 0, 660, 669 ),
        new CharMetrics( 722, 0, 21, -18, 706, 685 ), new CharMetrics( 778, 0, -24, 0, 799, 669 ),
        new CharMetrics( 389, 0, -32, 0, 406, 669 ), new CharMetrics( 500, 0, -46, -99, 524, 669 ),
        new CharMetrics( 667, 0, -21, 0, 702, 669 ), new CharMetrics( 611, 0, -22, 0, 590, 669 ),
        new CharMetrics( 889, 0, -29, -12, 917, 669 ), new CharMetrics( 722, 0, -27, -15, 748, 669 ),
        new CharMetrics( 722, 0, 27, -18, 691, 685 ), new CharMetrics( 611, 0, -27, 0, 613, 669 ),
        new CharMetrics( 722, 0, 27, -208, 691, 685 ), new CharMetrics( 667, 0, -29, 0, 623, 669 ),
        new CharMetrics( 556, 0, 2, -18, 526, 685 ), new CharMetrics( 611, 0, 50, 0, 650, 669 ),
        new CharMetrics( 722, 0, 67, -18, 744, 669 ), new CharMetrics( 667, 0, 65, -18, 715, 669 ),
        new CharMetrics( 889, 0, 65, -18, 940, 669 ), new CharMetrics( 667, 0, -24, 0, 694, 669 ),
        new CharMetrics( 611, 0, 73, 0, 659, 669 ), new CharMetrics( 611, 0, -11, 0, 590, 669 ),
        new CharMetrics( 333, 0, -37, -159, 362, 674 ), new CharMetrics( 278, 0, -1, -18, 279, 685 ),
        new CharMetrics( 333, 0, -56, -157, 343, 674 ), new CharMetrics( 570, 0, 67, 304, 503, 669 ),
        new CharMetrics( 500, 0, 0, -125, 500, -75 ), new CharMetrics( 333, 0, 128, 369, 332, 685 ),
        new CharMetrics( 500, 0, -21, -14, 455, 462 ), new CharMetrics( 500, 0, -14, -13, 444, 699 ),
        new CharMetrics( 444, 0, -5, -13, 392, 462 ), new CharMetrics( 500, 0, -21, -13, 517, 699 ),
        new CharMetrics( 444, 0, 5, -13, 398, 462 ), new CharMetrics( 333, 0, -169, -205, 446, 698 ),
        new CharMetrics( 500, 0, -52, -203, 478, 462 ), new CharMetrics( 556, 0, -13, -9, 498, 699 ),
        new CharMetrics( 278, 0, 2, -9, 263, 684 ), new CharMetrics( 278, 0, -189, -207, 279, 684 ),
        new CharMetrics( 500, 0, -23, -8, 483, 699 ), new CharMetrics( 278, 0, 2, -9, 290, 699 ),
        new CharMetrics( 778, 0, -14, -9, 722, 462 ), new CharMetrics( 556, 0, -6, -9, 493, 462 ),
        new CharMetrics( 500, 0, -3, -13, 441, 462 ), new CharMetrics( 500, 0, -120, -205, 446, 462 ),
        new CharMetrics( 500, 0, 1, -205, 471, 462 ), new CharMetrics( 389, 0, -21, 0, 389, 462 ),
        new CharMetrics( 389, 0, -19, -13, 333, 462 ), new CharMetrics( 278, 0, -11, -9, 281, 594 ),
        new CharMetrics( 556, 0, 15, -9, 492, 462 ), new CharMetrics( 444, 0, 16, -13, 401, 462 ),
        new CharMetrics( 667, 0, 16, -13, 614, 462 ), new CharMetrics( 500, 0, -46, -13, 469, 462 ),
        new CharMetrics( 444, 0, -94, -205, 392, 462 ), new CharMetrics( 389, 0, -43, -78, 368, 449 ),
        new CharMetrics( 348, 0, 5, -187, 436, 686 ), new CharMetrics( 220, 0, 66, -18, 154, 685 ),
        new CharMetrics( 348, 0, -129, -187, 302, 686 ), new CharMetrics( 570, 0, 54, 173, 516, 333 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 278, 0, 2, -9, 238, 462 ),
        new CharMetrics( 333, 0, 85, 516, 297, 697 ), new CharMetrics( 333, 0, 139, 516, 379, 697 ),
        new CharMetrics( 333, 0, 40, 516, 367, 690 ), new CharMetrics( 333, 0, 48, 536, 407, 655 ),
        new CharMetrics( 333, 0, 51, 553, 393, 623 ), new CharMetrics( 333, 0, 71, 516, 387, 678 ),
        new CharMetrics( 333, 0, 163, 525, 293, 655 ), new CharMetrics( 333, 0, 55, 525, 397, 655 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 333, 0, 127, 516, 340, 729 ),
        new CharMetrics( 333, 0, -80, -218, 156, 5 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 333, 0, 69, 516, 498, 697 ), new CharMetrics( 333, 0, -40, -173, 189, 44 ),
        new CharMetrics( 333, 0, 79, 516, 411, 690 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 389, 0, 19, -205, 322, 492 ), new CharMetrics( 500, 0, 42, -143, 439, 576 ),
        new CharMetrics( 500, 0, -32, -12, 510, 683 ), new CharMetrics( 500, 0, -26, 34, 526, 586 ),
        new CharMetrics( 500, 0, 33, 0, 628, 669 ), new CharMetrics( 220, 0, 66, -18, 154, 685 ),
        new CharMetrics( 500, 0, 36, -143, 459, 685 ), new CharMetrics( 333, 0, 55, 525, 397, 655 ),
        new CharMetrics( 747, 0, 30, -18, 718, 685 ), new CharMetrics( 266, 0, 16, 399, 330, 685 ),
        new CharMetrics( 500, 0, 12, 32, 468, 415 ), new CharMetrics( 606, 0, 51, 108, 555, 399 ),
        new CharMetrics( 333, 0, 2, 166, 271, 282 ), new CharMetrics( 747, 0, 30, -18, 718, 685 ),
        new CharMetrics( 333, 0, 51, 553, 393, 623 ), new CharMetrics( 400, 0, 83, 397, 369, 683 ),
        new CharMetrics( 570, 0, 33, 0, 537, 506 ), new CharMetrics( 300, 0, 2, 274, 313, 683 ),
        new CharMetrics( 300, 0, 17, 265, 321, 683 ), new CharMetrics( 333, 0, 139, 516, 379, 697 ),
        new CharMetrics( 576, 0, -60, -207, 516, 449 ), new CharMetrics( 500, 0, -57, -193, 562, 669 ),
        new CharMetrics( 250, 0, 51, 257, 199, 405 ), new CharMetrics( 333, 0, -80, -218, 156, 5 ),
        new CharMetrics( 300, 0, 30, 274, 301, 683 ), new CharMetrics( 300, 0, 56, 400, 347, 685 ),
        new CharMetrics( 500, 0, 12, 32, 468, 415 ), new CharMetrics( 750, 0, 7, -14, 721, 683 ),
        new CharMetrics( 750, 0, -9, -14, 723, 683 ), new CharMetrics( 750, 0, 7, -14, 726, 683 ),
        new CharMetrics( 500, 0, 30, -205, 421, 492 ), new CharMetrics( 667, 0, -67, 0, 593, 904 ),
        new CharMetrics( 667, 0, -67, 0, 593, 904 ), new CharMetrics( 667, 0, -67, 0, 593, 897 ),
        new CharMetrics( 667, 0, -67, 0, 593, 862 ), new CharMetrics( 667, 0, -67, 0, 593, 862 ),
        new CharMetrics( 667, 0, -67, 0, 593, 921 ), new CharMetrics( 944, 0, -64, 0, 918, 669 ),
        new CharMetrics( 667, 0, 32, -218, 677, 685 ), new CharMetrics( 667, 0, -27, 0, 653, 904 ),
        new CharMetrics( 667, 0, -27, 0, 653, 904 ), new CharMetrics( 667, 0, -27, 0, 653, 897 ),
        new CharMetrics( 667, 0, -27, 0, 653, 862 ), new CharMetrics( 389, 0, -32, 0, 406, 904 ),
        new CharMetrics( 389, 0, -32, 0, 412, 904 ), new CharMetrics( 389, 0, -32, 0, 420, 897 ),
        new CharMetrics( 389, 0, -32, 0, 445, 862 ), new CharMetrics( 722, 0, -31, 0, 700, 669 ),
        new CharMetrics( 722, 0, -27, -15, 748, 862 ), new CharMetrics( 722, 0, 27, -18, 691, 904 ),
        new CharMetrics( 722, 0, 27, -18, 691, 904 ), new CharMetrics( 722, 0, 27, -18, 691, 897 ),
        new CharMetrics( 722, 0, 27, -18, 691, 862 ), new CharMetrics( 722, 0, 27, -18, 691, 862 ),
        new CharMetrics( 570, 0, 48, 16, 522, 490 ), new CharMetrics( 722, 0, 27, -125, 691, 764 ),
        new CharMetrics( 722, 0, 67, -18, 744, 904 ), new CharMetrics( 722, 0, 67, -18, 744, 904 ),
        new CharMetrics( 722, 0, 67, -18, 744, 897 ), new CharMetrics( 722, 0, 67, -18, 744, 862 ),
        new CharMetrics( 611, 0, 73, 0, 659, 904 ), new CharMetrics( 611, 0, -27, 0, 573, 669 ),
        new CharMetrics( 500, 0, -200, -200, 473, 705 ), new CharMetrics( 500, 0, -21, -14, 455, 697 ),
        new CharMetrics( 500, 0, -21, -14, 463, 697 ), new CharMetrics( 500, 0, -21, -14, 455, 690 ),
        new CharMetrics( 500, 0, -21, -14, 491, 655 ), new CharMetrics( 500, 0, -21, -14, 471, 655 ),
        new CharMetrics( 500, 0, -21, -14, 455, 729 ), new CharMetrics( 722, 0, -5, -13, 673, 462 ),
        new CharMetrics( 444, 0, -24, -218, 392, 462 ), new CharMetrics( 444, 0, 5, -13, 398, 697 ),
        new CharMetrics( 444, 0, 5, -13, 435, 697 ), new CharMetrics( 444, 0, 5, -13, 423, 690 ),
        new CharMetrics( 444, 0, 5, -13, 443, 655 ), new CharMetrics( 278, 0, 2, -9, 260, 697 ),
        new CharMetrics( 278, 0, 2, -9, 352, 697 ), new CharMetrics( 278, 0, -2, -9, 325, 690 ),
        new CharMetrics( 278, 0, 2, -9, 360, 655 ), new CharMetrics( 500, 0, -3, -13, 454, 699 ),
        new CharMetrics( 556, 0, -6, -9, 504, 655 ), new CharMetrics( 500, 0, -3, -13, 441, 697 ),
        new CharMetrics( 500, 0, -3, -13, 463, 697 ), new CharMetrics( 500, 0, -3, -13, 451, 690 ),
        new CharMetrics( 500, 0, -3, -13, 491, 655 ), new CharMetrics( 500, 0, -3, -13, 466, 655 ),
        new CharMetrics( 570, 0, 33, -29, 537, 535 ), new CharMetrics( 500, 0, -3, -119, 441, 560 ),
        new CharMetrics( 556, 0, 15, -9, 492, 697 ), new CharMetrics( 556, 0, 15, -9, 492, 697 ),
        new CharMetrics( 556, 0, 15, -9, 492, 690 ), new CharMetrics( 556, 0, 15, -9, 494, 655 ),
        new CharMetrics( 444, 0, -94, -205, 435, 697 ), new CharMetrics( 500, 0, -120, -205, 446, 699 ),
        new CharMetrics( 444, 0, -94, -205, 438, 655 )};

    public SerifBoldItalic()
    {
        super( false, 699, -205, new CharMetrics( 0, 0, -200, -218, 996, 921 ), metrics );
    }

}
